package com.blacksmith.quranlib.data.model

data class WordTextEntity(
    val id: Int,
    val location: String?,
    val surah: Int?,
    val ayah: Int?,
    val word: Int?,
    val text: String?
)
