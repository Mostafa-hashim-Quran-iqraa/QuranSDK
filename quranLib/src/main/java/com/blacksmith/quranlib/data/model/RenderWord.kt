package com.blacksmith.quranlib.data.model

import java.io.Serializable

data class RenderWord(
    val text: String,
    val x: Float = 0f,
    val y: Float = 0f
) : Serializable {

}