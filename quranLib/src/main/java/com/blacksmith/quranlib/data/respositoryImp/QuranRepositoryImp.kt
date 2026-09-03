package com.blacksmith.quranlib.data.respositoryImp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.blacksmith.quranlib.data.local.database.DatabaseProvider
import com.blacksmith.quranlib.data.model.AyaModel
import com.blacksmith.quranlib.data.model.JuzIndexItem
import com.blacksmith.quranlib.data.model.PageEntity
import com.blacksmith.quranlib.data.model.SurahIndexEntry
import com.blacksmith.quranlib.data.model.SurahListItem
import com.blacksmith.quranlib.data.model.WordEntity
import com.blacksmith.quranlib.data.util.QuranDownloadManager
import com.blacksmith.quranlib.domain.parseFromJson
import com.blacksmith.quranlib.domain.remote.QuranRepository
import com.blacksmith.quranlib.domain.response.QuranFileResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class QuranRepositoryImp @Inject constructor(
    private val context: Context
) : QuranRepository {

    /**
     * Opens the downloaded DB lazily.
     * QuranDataGuard guarantees the file exists before any screen that needs it is shown.
     */
    private val db: SQLiteDatabase by lazy {
        DatabaseProvider.openDatabase(context, QuranDownloadManager.QURAN_DB_FILE_NAME)
    }

    @Volatile
    private var _cachedQuranData: QuranFileResponseModel? = null

    override suspend fun getPages(): List<PageEntity> =
        withContext(Dispatchers.IO) {
            val list = mutableListOf<PageEntity>()

            val cursor = db.rawQuery("SELECT * FROM pages_v4", null)
            cursor.use {
                while (it.moveToNext()) {
                    list.add(
                        PageEntity(
                            page_number = it.getInt(it.getColumnIndexOrThrow("page_number")),
                            line_number = it.getInt(it.getColumnIndexOrThrow("line_number")),
                            line_type = it.getString(it.getColumnIndexOrThrow("line_type")),
                            is_centered = it.getInt(it.getColumnIndexOrThrow("is_centered")),
                            first_word_id = it.getInt(it.getColumnIndexOrThrow("first_word_id")),
                            last_word_id = it.getInt(it.getColumnIndexOrThrow("last_word_id")),
                            surah_number = it.getInt(it.getColumnIndexOrThrow("surah_number")),
                            chapter_number = it.getInt(it.getColumnIndexOrThrow("chapter_number"))
                        )
                    )
                }
            }

            list
        }

    override suspend fun getWords(): List<WordEntity> = withContext(Dispatchers.IO) {
        val list = mutableListOf<WordEntity>()
        val cursor = db.rawQuery("SELECT * FROM words", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    WordEntity(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        location = it.getString(it.getColumnIndexOrThrow("location")),
                        surah = it.getInt(it.getColumnIndexOrThrow("surah")),
                        ayah = it.getInt(it.getColumnIndexOrThrow("ayah")),
                        word = it.getInt(it.getColumnIndexOrThrow("word")),
                        glyphV2 = it.getString(it.getColumnIndexOrThrow("glyph_v2")),
                        glyphV4 = it.getString(it.getColumnIndexOrThrow("glyph_v4")),
                        wordText = it.getString(it.getColumnIndexOrThrow("word_text"))
                    )
                )
            }
        }
        list
    }

    override suspend fun getQuranData(context: Context): QuranFileResponseModel {
        _cachedQuranData?.let { return it }
        var quranFileResponseModel = QuranFileResponseModel()
        try {
            // QuranDataGuard guarantees the downloaded file exists before we reach here
            val quranText = QuranDownloadManager.jsonFile(context)
                .bufferedReader().use { it.readText() }
            quranFileResponseModel =
                parseFromJson<QuranFileResponseModel>(quranText) ?: QuranFileResponseModel()
            _cachedQuranData = quranFileResponseModel
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return quranFileResponseModel
    }

    override suspend fun searchAyas(context: Context, query: String): List<AyaModel> =
        withContext(Dispatchers.IO) {
            // Strip tashkeel from the query so a diacritized paste (e.g. copied from the
            // Mushaf page) still matches. Stripping tashkeel alone isn't enough though:
            // `text` (Uthmani rasm, what's shown/copied on the Mushaf page) and aya_text
            // (simplified spelling) differ in actual letters too, not just marks -- e.g.
            // "ٱ" (alef wasla) in text vs plain "ا" in aya_text, or "ملك" in text vs
            // "مالك" in aya_text. So a query is checked against both: aya_text as-is
            // (for a normally-typed query), and text with its own tashkeel stripped
            // (for a query pasted straight from the Mushaf, still in Uthmani rasm).
            //
            // The Mushaf's own "copy" action appends a citation line after the aya text
            // (e.g. "...\nسورة الفاتحة - آية 6"), which would never be found as a
            // substring of any aya. That citation always starts on a new line, so drop
            // everything from the first newline onward before matching.
            val normalizedQuery = query.substringBefore('\n').trim().stripTashkeel()
            Log.d("QURAN_SEARCH_DEBUG", "raw=\"$query\" normalized=\"$normalizedQuery\"")
            if (normalizedQuery.length < 2) return@withContext emptyList()
            val data = _cachedQuranData ?: getQuranData(context)
            Log.d("QURAN_SEARCH_DEBUG", "surasCount=${data.suras?.size}")
            val results = mutableListOf<AyaModel>()
            data.suras?.forEach { surah ->
                surah.ayas?.forEach { aya ->
                    val ayaSearchText = aya.aya_text ?: ""
                    val matches = ayaSearchText.contains(normalizedQuery) ||
                        (aya.text ?: "").stripTashkeel().contains(normalizedQuery)
                    if (matches) {
                        results.add(aya.copy(surah = surah))
                        if (results.size >= 30) {
                            Log.d("QURAN_SEARCH_DEBUG", "resultsCount=${results.size} (capped)")
                            return@withContext results
                        }
                    }
                }
            }
            Log.d("QURAN_SEARCH_DEBUG", "resultsCount=${results.size}")
            results
        }

    override suspend fun getJuzIndexWithSurasInsideJuz(context: Context): List<JuzIndexItem> =
        withContext(Dispatchers.IO) {
            val data = _cachedQuranData ?: getQuranData(context)
            // Build juzId → first_aya_id lookup from the outer chapters list
            // e.g. Juz 2 → 149  (global aya ID that opens Juz 2)
            val juzFirstAyaId: Map<Int, Int> = data.chapters
                ?.mapNotNull { chapter ->
                    val juzId = chapter.id?.toIntOrNull() ?: return@mapNotNull null
                    val firstAyaId =
                        chapter.first_aya_id?.takeIf { it > 0 } ?: return@mapNotNull null
                    juzId to firstAyaId
                }
                ?.toMap()
                ?: emptyMap()
            val juzSurahsMap = mutableMapOf<Int, MutableList<SurahIndexEntry>>()
            data.suras?.forEach { surah ->
                val surahId = surah.id?.toIntOrNull() ?: return@forEach
                val surahNameAr = surah.name_ar ?: ""
                // Build a fast lookup: global aya id → aya text  (only for THIS surah)
                val ayaTextById: Map<Int, String> = surah.ayas
                    ?.mapNotNull { aya ->
                        val id = aya.id?.toIntOrNull() ?: return@mapNotNull null
                        val text = aya.text ?: return@mapNotNull null
                        id to text
                    }
                    ?.toMap()
                    ?: emptyMap()
                val fallbackText = surah.ayas?.firstOrNull()?.text ?: ""
                surah.chapters?.forEach inner@{ juzEntry ->
                    val juzId = juzEntry.id?.toIntOrNull()?.takeIf { it in 1..30 } ?: return@inner
                    val pageInJuz = juzEntry.page_number?.takeIf { it > 0 }
                        ?: surah.page_number
                        ?: 1
                    // Use the text of the aya that opens this juz if it belongs to
                    // this surah; otherwise fall back to the surah's own first aya.
                    val previewText = juzFirstAyaId[juzId]
                        ?.let { firstId -> ayaTextById[firstId] }
                        ?: fallbackText
                    juzSurahsMap.getOrPut(juzId) { mutableListOf() }.add(
                        SurahIndexEntry(
                            surahId = surahId,
                            surahNameAr = surahNameAr,
                            firstAyaText = previewText,
                            page = pageInJuz,
                        )
                    )
                }
            }
            // Build final 30-juz list sorted by page within each juz
            (1..30).map { juzId ->
                val outerChapter = data.chapters?.find { it.id?.toIntOrNull() == juzId }
                JuzIndexItem(
                    juzId = juzId,
                    juzNameAr = outerChapter?.name_ar ?: "",
                    surahs = (juzSurahsMap[juzId] ?: emptyList()).sortedBy { it.page },
                )
            }
        }

    override suspend fun getSurahList(context: Context): List<SurahListItem> =
        withContext(Dispatchers.IO) {
            val data = _cachedQuranData ?: getQuranData(context)
            data.suras
                ?.mapNotNull { surah ->
                    SurahListItem(
                        surahId = surah.id?.toIntOrNull() ?: return@mapNotNull null,
                        surahNameAr = surah.name_ar ?: "",
                        ayaCount = surah.aya_numbers ?: 0,
                        page = surah.page_number ?: 1,
                    )
                }
                ?.sortedBy { it.surahId }
                ?: emptyList()
        }
}

/**
 * True for Arabic tashkeel / Quranic annotation marks (harakat, sukun, superscript
 * alef, Uthmani recitation signs) plus tatweel (kashida), the elongation character
 * used to visually stretch letter connections in the printed Mushaf layout. Uses
 * code-point ranges rather than embedding the marks themselves in a string literal,
 * since combining diacritics are awkward to keep intact through source-file edits.
 */
private fun Char.isTashkeel(): Boolean {
    val c = code
    return c in 0x0610..0x061A ||
        c in 0x064B..0x065F ||
        c == 0x0670 ||
        c == 0x0640 || // tatweel
        c in 0x06D6..0x06ED ||
        c in 0x08D4..0x08FF
}

/**
 * Strips tashkeel/tatweel and collapses whitespace runs to a single space (trimmed).
 * `text` (Uthmani rasm) and word-by-word copied text can otherwise carry stray/extra
 * spacing -- e.g. an empty leading "word" -- that a plain [String.trim] won't catch.
 */
private fun String.stripTashkeel(): String {
    val sb = StringBuilder(length)
    var lastWasSpace = false
    for (ch in this) {
        if (ch.isTashkeel()) continue
        if (ch.isWhitespace()) {
            if (!lastWasSpace) sb.append(' ')
            lastWasSpace = true
        } else {
            sb.append(ch)
            lastWasSpace = false
        }
    }
    return sb.toString().trim()
}
