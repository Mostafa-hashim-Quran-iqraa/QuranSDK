package com.blacksmith.quranlib.data.model

import java.io.Serializable

data class SurahModel(
    var id: String? = "",
    var number: String? = "",
    var name: String? = "",
    var ligature: String = "",

//    var en_name_translation: String? = "",
    var type: String? = "",
    var description: String? = "",
    var verses_count: String? = "",
//    var ayahs_numbers: Int? = 0,
    var page: Int? = 0,
    var selected: Boolean = false,
    var chapter_id: String? = "",
    var name_ar: String? = "",
    var name_en: String? = "",
    var achievementId: String? = "",
    var page_number: Int? = 0,
    var aya_numbers: Int? = 0,
    var ayas: ArrayList<AyaModel>? = ArrayList(),
    var chapters: ArrayList<ChapterModel>? = ArrayList(),
    var completed: Int? = 0,
    var recited_verses: Int? = 0,
    var remaining_verses: Int? = 0,
) : Serializable {

}