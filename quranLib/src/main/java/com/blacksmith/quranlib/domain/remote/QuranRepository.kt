package com.blacksmith.quranlib.domain.remote

import android.content.Context
import com.blacksmith.quranlib.data.model.AyaModel
import com.blacksmith.quranlib.data.model.JuzIndexItem
import com.blacksmith.quranlib.data.model.PageEntity
import com.blacksmith.quranlib.data.model.QuranWordModel
import com.blacksmith.quranlib.data.model.WordEntity
import com.blacksmith.quranlib.data.model.WordTextEntity
import com.blacksmith.quranlib.domain.response.QuranFileResponseModel

interface QuranRepository {

    suspend fun getQuranData(context: Context): QuranFileResponseModel

    suspend fun getPages(versionNumber: Int): List<PageEntity>
    suspend fun getWords(): List<WordEntity>

    suspend fun searchAyas(context: Context, query: String): List<AyaModel>

    /** Returns 30 Juz entries, each with the Surahs that start inside it. */
    suspend fun getJuzIndex(context: Context): List<JuzIndexItem>

}
