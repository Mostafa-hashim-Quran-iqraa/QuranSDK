package com.blacksmith.quranlib.data.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

/**
 * Handles downloading, unzipping and cleanup for Quran assets that are
 * fetched at runtime (DB, JSON, fonts).
 *
 * Internal storage layout
 * ─────────────────────────────────────────────────────────────────────────────
 *  quran.db    → context.getDatabasePath("quran.db")
 *  quran.json  → context.filesDir / quran / quran.json
 *  fonts 1441  → context.filesDir / fonts / 1441normal / p{n}.ttf
 *  fonts 1441c → context.filesDir / fonts / 1441colored / p{n}.ttf
 */
object QuranDownloadManager {

    // ── Path helpers ──────────────────────────────────────────────────────────
    const val QURAN_DB_FILE_NAME = "quran.db"
    const val QURAN_JSON_FILE_NAME = "quran/quran.json"
    const val FONTS_PRINT_1441_NORMAL_PATH = "fonts/1441normal"
    const val FONTS_PRINT_1441_COLORED_PATH = "fonts/1441colored"
    const val QURAN_DB_URL =
        "https://quran-iqraa-bucket.s3.us-east-2.amazonaws.com/app/quran/assets/quranDB.zip"
    const val QURAN_JSON_URL =
        "https://quran-iqraa-bucket.s3.us-east-2.amazonaws.com/app/quran/assets/quranJson.zip"

    const val FONTS_PRINT_1441_URL =
        "https://quran-iqraa-bucket.s3.us-east-2.amazonaws.com/app/quran/assets/fontsPrint1441Normal.zip"
    const val FONTS_PRINT_1441_COLORED_URL =
        "https://quran-iqraa-bucket.s3.us-east-2.amazonaws.com/app/quran/assets/fontsPrint1441Colored.zip"

    fun dbFile(context: Context): File = context.getDatabasePath(QURAN_DB_FILE_NAME)
    fun jsonFile(context: Context): File = File(context.filesDir, QURAN_JSON_FILE_NAME)
    fun fontsDir(context: Context, version: Int): File = File(
        context.filesDir,
        if (version == QuranConstants.VERSION_KING_FAHD_1441_COLORED) FONTS_PRINT_1441_COLORED_PATH
        else FONTS_PRINT_1441_NORMAL_PATH
    )

    fun isDbReady(context: Context): Boolean = dbFile(context).exists()

    fun isJsonReady(context: Context): Boolean = jsonFile(context).exists()

    /**
     * Fonts are only downloaded for versions 1441 and 1441-colored.
     * Other versions fall back to bundled assets, so they are always "ready".
     *
     * We rely on a sentinel marker file written after a successful extraction
     * rather than checking for a specific font file, because the zip may
     * contain a top-level subdirectory that shifts all file paths one level deeper.
     */
    fun isFontsReady(context: Context, version: Int): Boolean {
        if (version != QuranConstants.VERSION_KING_FAHD_1441 &&
            version != QuranConstants.VERSION_KING_FAHD_1441_COLORED
        ) return true
        return fontsReadyMarker(context, version).exists()
    }

    // ── Sentinel marker helpers ───────────────────────────────────────────────

    /** Marker written after fonts are successfully extracted for [version]. */
    private fun fontsReadyMarker(context: Context, version: Int): File =
        File(context.filesDir, ".quran_fonts_ready_$version")

    /** True when DB + JSON are ready (use for data-only scenarios, no page rendering). */
    fun isDataReady(context: Context): Boolean = isDbReady(context) && isJsonReady(context)

    /** True when DB + JSON + fonts for [version] are all ready. */
    fun isAllReady(context: Context, version: Int): Boolean =
        isDataReady(context) && isFontsReady(context, version)

    // ── High-level setup steps ────────────────────────────────────────────────

    suspend fun setupDb(
        context: Context,
        onProgress: suspend (Int) -> Unit,
        onExtracting: suspend () -> Unit,
    ) {
        val destDir = dbFile(context).parentFile ?: return
        downloadAndUnzip(QURAN_DB_URL, context, destDir, onProgress, onExtracting)
    }

    suspend fun setupJson(
        context: Context,
        onProgress: suspend (Int) -> Unit,
        onExtracting: suspend () -> Unit,
    ) {
        val destDir = jsonFile(context).parentFile ?: return
        downloadAndUnzip(QURAN_JSON_URL, context, destDir, onProgress, onExtracting)
    }

    suspend fun setupFonts(
        context: Context,
        version: Int,
        onProgress: suspend (Int) -> Unit,
        onExtracting: suspend () -> Unit,
    ) {
        val url = when (version) {
            QuranConstants.VERSION_KING_FAHD_1441 -> FONTS_PRINT_1441_URL
            QuranConstants.VERSION_KING_FAHD_1441_COLORED -> FONTS_PRINT_1441_COLORED_URL
            else -> return   // nothing to download
        }
        downloadAndUnzip(url, context, fontsDir(context, version), onProgress, onExtracting)
        // Write sentinel so isFontsReady returns true on future launches
        fontsReadyMarker(context, version).createNewFile()
    }

    // ── Core: download → unzip → delete zip ──────────────────────────────────

    private suspend fun downloadAndUnzip(
        url: String,
        context: Context,
        destDir: File,
        onProgress: suspend (Int) -> Unit,
        onExtracting: suspend () -> Unit,
    ) = withContext(Dispatchers.IO) {
        destDir.mkdirs()
        val zipFile = File(context.cacheDir, "quran_dl_${System.currentTimeMillis()}.zip")
        try {
            downloadFile(url, zipFile, onProgress)
            onExtracting()
            unzip(zipFile, destDir)
        } finally {
            // Always delete the zip to free space
            zipFile.delete()
        }
    }

    private suspend fun downloadFile(
        url: String,
        dest: File,
        onProgress: suspend (Int) -> Unit,
    ) = withContext(Dispatchers.IO) {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = 30_000
        conn.readTimeout = 60_000
        conn.connect()
        val totalBytes = conn.contentLengthLong.takeIf { it > 0 } ?: -1L
        try {
            conn.inputStream.use { input ->
                FileOutputStream(dest).use { out ->
                    val buffer = ByteArray(8 * 1024)
                    var downloaded = 0L
                    var lastPct = -1
                    var n: Int
                    while (input.read(buffer).also { n = it } != -1) {
                        out.write(buffer, 0, n)
                        downloaded += n
                        if (totalBytes > 0) {
                            val pct = ((downloaded * 100) / totalBytes).toInt().coerceIn(0, 99)
                            if (pct != lastPct) {
                                lastPct = pct
                                onProgress(pct)
                            }
                        }
                    }
                }
            }
        } finally {
            conn.disconnect()
        }
    }

    private fun unzip(zipFile: File, destDir: File) {
        ZipInputStream(zipFile.inputStream().buffered()).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val outFile = File(destDir, entry.name)
                if (entry.isDirectory) {
                    outFile.mkdirs()
                } else {
                    outFile.parentFile?.mkdirs()
                    FileOutputStream(outFile).use { fos -> zis.copyTo(fos) }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }
}
