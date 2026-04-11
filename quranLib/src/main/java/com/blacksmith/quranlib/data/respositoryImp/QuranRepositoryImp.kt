package com.blacksmith.quranlib.data.respositoryImp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.blacksmith.quranlib.data.model.PageEntity
import com.blacksmith.quranlib.data.model.WordEntity
import com.blacksmith.quranlib.data.model.WordTextEntity
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

    override suspend fun getPages(): List<PageEntity> = withContext(Dispatchers.IO) {
        val list = mutableListOf<PageEntity>()

        val cursor = db.rawQuery("SELECT * FROM pages", null)

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
    override suspend fun getWords() : List<WordEntity> = withContext(Dispatchers.IO) {
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
                        text = it.getString(it.getColumnIndexOrThrow("text"))
                    )
                )
            }
        }

        list
    }
    override suspend fun getWordsText(): List<WordTextEntity> = withContext(Dispatchers.IO) {
        val list = mutableListOf<WordTextEntity>()

        val cursor = db.rawQuery("SELECT * FROM words_text", null)

        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    WordTextEntity(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        location = it.getString(it.getColumnIndexOrThrow("location")),
                        surah = it.getInt(it.getColumnIndexOrThrow("surah")),
                        ayah = it.getInt(it.getColumnIndexOrThrow("ayah")),
                        word = it.getInt(it.getColumnIndexOrThrow("word")),
                        text = it.getString(it.getColumnIndexOrThrow("text"))
                    )
                )
            }
        }
        list
    }

    override suspend fun getQuranData(context: Context): QuranFileResponseModel {
        var quranFileResponseModel = QuranFileResponseModel()
        try {
            val quranText = context.assets.open("quran.json").bufferedReader().use {
                it.readText()
            }
            quranFileResponseModel =
                parseFromJson<QuranFileResponseModel>(quranText) ?: QuranFileResponseModel()
        } catch (e: IOException) {
            // Handle exceptions here
            e.printStackTrace()
        }
        return quranFileResponseModel
    }
}