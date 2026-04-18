package com.blacksmith.quranlib.data.respositoryImp

import android.content.Context
import com.blacksmith.quranlib.data.local.database.DatabaseCopier
import com.blacksmith.quranlib.data.local.database.DatabaseProvider
import com.blacksmith.quranlib.data.model.PageEntity
import com.blacksmith.quranlib.data.model.WordEntity
import com.blacksmith.quranlib.domain.remote.QuranRepository
import com.blacksmith.quranlib.domain.response.QuranFileResponseModel

// في الـ SDK
class QuranSDK private constructor(
    private val repository: QuranRepository
) {
    companion object {
        @Volatile
        private var instance: QuranSDK? = null

        fun init(context: Context): QuranSDK {
            return instance ?: synchronized(this) {
                instance ?: run {
                    DatabaseCopier.copyDatabase(context, "quran.db")
                    val db = DatabaseProvider.openDatabase(context, "quran.db")
                    val repo = QuranRepositoryImp(db)
                    QuranSDK(repo).also { instance = it }
                }
            }
        }

        fun getInstance(): QuranSDK {
            return instance ?: error("QuranSDK not initialized. Call QuranSDK.init(context) first.")
        }
    }

    suspend fun getPages(versionNumber: Int): List<PageEntity> =
        repository.getPages(versionNumber)

    suspend fun getWords(): List<WordEntity> =
        repository.getWords()

    suspend fun getQuranData(context: Context): QuranFileResponseModel =
        repository.getQuranData(context)
}