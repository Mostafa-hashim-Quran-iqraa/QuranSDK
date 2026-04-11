package com.blacksmith.quranlib.data.model

import java.io.Serializable

data class AyaModel(
    var id: String? = "",
    var number: String? = "",
    var text: String? = "",
    var number_in_surah: String? = "",
    var surah_id: Int? = 0,
    var page: Int? = 0,
    var querter_hizb: Int? = 0,
    var chapter_id: Int? = 0,
    var sajda: Int? = 0,
    var quarter_mark: Int? = 0,
    var surah: SurahModel? = SurahModel(),
    var selected: Boolean = false,
    var sura: String? = "",
    var aya: String? = "",
    var line_start: String? = "",
    var line_end: String? = "",
    var aya_text: String? = "",
) : Serializable {

}