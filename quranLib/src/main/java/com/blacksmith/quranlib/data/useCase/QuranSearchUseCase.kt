package com.blacksmith.quranlib.data.useCase

import android.content.Context
import com.blacksmith.quranlib.data.model.AyaModel
import com.blacksmith.quranlib.data.model.JuzIndexItem
import com.blacksmith.quranlib.domain.remote.QuranRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranSearchUseCase @Inject constructor(
    private val repository: QuranRepository,
) {
    /**
     * Search Quran verses by text (without tashkeel).
     * Returns up to 30 matching [AyaModel] with surah info populated.
     * Query must be at least 2 characters, otherwise returns empty list.
     */
    suspend fun searchAyas(context: Context, query: String): List<AyaModel> =
        repository.searchAyas(context, query)

    /**
     * Returns the full Quran index: 30 Juz, each with the Surahs that START inside it.
     * Result is cached after the first call (reads from the same quran.json cache).
     */
    suspend fun getJuzIndex(context: Context): List<JuzIndexItem> =
        repository.getJuzIndexWithSurasInsideJuz(context)
}