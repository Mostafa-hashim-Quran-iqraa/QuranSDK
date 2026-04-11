package com.blacksmith.quranlib.domain.response

import com.blacksmith.quranlib.data.model.ChapterModel
import com.blacksmith.quranlib.data.model.SurahModel

class QuranFileResponseModel:ParentResponseModel() {

    var suras: ArrayList<SurahModel>? = kotlin.collections.ArrayList()
    var chapters: ArrayList<ChapterModel>? = kotlin.collections.ArrayList()
}