package com.blacksmith.quranlib.data.model

import java.io.Serializable

data class QuranWordModel(
    val id: Int,
    val surah: String,
    val ayah: String,
    val word: String,
    val location: String,
    val text: String
) : Serializable {

}