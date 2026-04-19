package com.blacksmith.quranlib.data.respositoryImp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.blacksmith.quranlib.data.model.AyaModel
import com.blacksmith.quranlib.data.model.JuzIndexItem
import com.blacksmith.quranlib.data.model.PageEntity
import com.blacksmith.quranlib.data.model.SurahIndexEntry
import com.blacksmith.quranlib.data.model.WordEntity
import com.blacksmith.quranlib.data.util.QuranConstants
import com.blacksmith.quranlib.domain.parseFromJson
import com.blacksmith.quranlib.domain.remote.QuranRepository
import com.blacksmith.quranlib.domain.response.QuranFileResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class QuranRepositoryImp @Inject constructor(
    private val db: SQLiteDatabase
) : QuranRepository {

    @Volatile
    private var _cachedQuranData: QuranFileResponseModel? = null

    override suspend fun getPages(versionNumber: Int): List<PageEntity> =
        withContext(Dispatchers.IO) {
            val list = mutableListOf<PageEntity>()

            val cursor = if (versionNumber == QuranConstants.VERSION_KING_FAHD_1421) db.rawQuery("SELECT * FROM pages_v2", null)
            else db.rawQuery("SELECT * FROM pages_v4", null)

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
            val quranText = context.assets.open("quran.json").bufferedReader().use {
                it.readText()
            }
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
            val normalizedQuery = query.trim()
            if (normalizedQuery.length < 2) return@withContext emptyList()

            val data = _cachedQuranData ?: getQuranData(context)
            val results = mutableListOf<AyaModel>()

            data.suras?.forEach { surah ->
                surah.ayas?.forEach { aya ->
                    val ayaSearchText = aya.aya_text ?: ""
                    if (ayaSearchText.contains(normalizedQuery)) {
                        results.add(aya.copy(surah = surah))
                        if (results.size >= 30) return@withContext results
                    }
                }
            }
            results
        }

    override suspend fun getJuzIndexWithSurasInsideJuz(context: Context): List<JuzIndexItem> =
        withContext(Dispatchers.IO) {
            val data = _cachedQuranData ?: getQuranData(context)

            // Build juzId → first_aya_id lookup from the outer chapters list
            // e.g. Juz 2 → 149  (global aya ID that opens Juz 2)
            val juzFirstAyaId: Map<Int, Int> = data.chapters
                ?.mapNotNull { chapter ->
                    val juzId     = chapter.id?.toIntOrNull() ?: return@mapNotNull null
                    val firstAyaId = chapter.first_aya_id?.takeIf { it > 0 } ?: return@mapNotNull null
                    juzId to firstAyaId
                }
                ?.toMap()
                ?: emptyMap()

            val juzSurahsMap = mutableMapOf<Int, MutableList<SurahIndexEntry>>()

            data.suras?.forEach { surah ->
                val surahId     = surah.id?.toIntOrNull() ?: return@forEach
                val surahNameAr = surah.name_ar ?: ""

                // Build a fast lookup: global aya id → aya text  (only for THIS surah)
                val ayaTextById: Map<Int, String> = surah.ayas
                    ?.mapNotNull { aya ->
                        val id   = aya.id?.toIntOrNull() ?: return@mapNotNull null
                        val text = aya.text ?: return@mapNotNull null
                        id to text
                    }
                    ?.toMap()
                    ?: emptyMap()

                val fallbackText = surah.ayas?.firstOrNull()?.text ?: ""

                surah.chapters?.forEach inner@{ juzEntry ->
                    val juzId     = juzEntry.id?.toIntOrNull()?.takeIf { it in 1..30 } ?: return@inner
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
                            surahId      = surahId,
                            surahNameAr  = surahNameAr,
                            firstAyaText = previewText,
                            page         = pageInJuz,
                        )
                    )
                }
            }

            // Build final 30-juz list sorted by page within each juz
            (1..30).map { juzId ->
                val outerChapter = data.chapters?.find { it.id?.toIntOrNull() == juzId }
                JuzIndexItem(
                    juzId     = juzId,
                    juzNameAr = outerChapter?.name_ar ?: "الجزء $juzId",
                    surahs    = (juzSurahsMap[juzId] ?: emptyList()).sortedBy { it.page },
                )
            }
        }
}
