package com.blacksmith.quranlib.data.model

import androidx.compose.runtime.Stable

@Stable
data class WordModel(
    val id: Int,
    val text: String,        // من words
    val wordText: String,    // من words_text
    val surahId: Int,
    val surahName: String,
    val chapterId: Int,
    val chapterName: String,
    val ayah: Int,
    val word: Int,
    val location: String?,
){
}
