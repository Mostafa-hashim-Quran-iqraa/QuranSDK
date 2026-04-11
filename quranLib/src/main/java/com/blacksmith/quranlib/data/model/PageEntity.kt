package com.blacksmith.quranlib.data.model

data class PageEntity(
    val id: Int = 0,
    val page_number: Int,
    val line_number: Int,
    val line_type: String?,
    val is_centered: Int?,
    val first_word_id: Int?,
    val last_word_id: Int?,
    val surah_number: Int?,
    val chapter_number: Int?
)
