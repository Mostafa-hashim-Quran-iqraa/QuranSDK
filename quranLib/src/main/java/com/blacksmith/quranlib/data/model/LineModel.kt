package com.blacksmith.quranlib.data.model

data class LineModel(
    val lineNumber: Int,
    val isCentered: Boolean,
    val surahId: Int,
    val surahName: String,
    var surahLigature: String = "",
    val chapterId: Int,
    val chapterName: String,
    val line_type: String?,
    val words: List<WordModel>
){
    companion object{
        const val LINE_TYPE_SURAH_NAME = "surah_name"
        const val LINE_TYPE_BASMALAH = "basmallah"
    }
}
