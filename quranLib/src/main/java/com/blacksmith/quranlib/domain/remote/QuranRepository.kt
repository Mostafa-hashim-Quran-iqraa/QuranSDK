package com.blacksmith.quranlib.domain.remote

import android.content.Context
import com.blacksmith.quranlib.data.model.PageEntity
import com.blacksmith.quranlib.data.model.QuranWordModel
import com.blacksmith.quranlib.data.model.WordEntity
import com.blacksmith.quranlib.data.model.WordTextEntity
import com.blacksmith.quranlib.domain.response.QuranFileResponseModel

interface QuranRepository {

    suspend fun getQuranData(context: Context): QuranFileResponseModel

    suspend fun getPages(): List<PageEntity>
    suspend fun getWords(): List<WordEntity>
    suspend fun getWordsText(): List<WordTextEntity>

}