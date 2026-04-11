package com.blacksmith.quranlib.data.model

import java.io.Serializable

data class ChapterModel(
    var id: String? = "",
    var number: String? = "",
    var page_number: Int? = 0,
    var name: String? = "",
    var name_ar: String? = "",
    var name_en: String? = "",
    var created_at: String? = "",
    var updated_at: String? = "",
    var selected: Boolean = false,
    var start_verses_in_chapter: ArrayList<AyaModel>? = null,
) : Serializable {
}