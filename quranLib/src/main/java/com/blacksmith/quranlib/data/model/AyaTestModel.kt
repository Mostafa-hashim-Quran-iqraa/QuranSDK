package com.blacksmith.quranlib.data.model

import java.io.Serializable

data class AyaTestModel(
    val surah: String,
    val ayah: String,
    val words: List<QuranWordModel>
) : Serializable {

}