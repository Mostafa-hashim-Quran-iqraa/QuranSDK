package com.blacksmith.quranlib.data.model

import java.io.Serializable

data class QuranPageModel(
    val pageNumber: Int,
    val lines: List<LineModel>,
    val surahModel: SurahModel,
    val chapterModel: ChapterModel,
) : Serializable {

}