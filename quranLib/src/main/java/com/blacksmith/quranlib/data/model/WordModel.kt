package com.blacksmith.quranlib.data.model

import androidx.compose.runtime.Stable

@Stable
data class WordModel(
    val id: Int,
    val glyph: String,        // من words
    val wordText: String,    // من words_text
    val surahId: Int,
    val surahName: String,
    val chapterId: Int,
    val chapterName: String,
    val ayah: Int,
    val word: Int,
    val location: String?,

    // ── Precomputed layout ───────────────────────────────────────────────────
    // layoutX, layoutWidth: موقع وعرض الكلمة في الـ canvas (screen space)
    // lineTop, lineBottom: حدود السطر للـ Y hit-test الصحيح
    var layoutX: Float = 0f,
    var layoutWidth: Float = 0f,
    var lineTop: Float = 0f,
    var lineBottom: Float = 0f,
){
}
