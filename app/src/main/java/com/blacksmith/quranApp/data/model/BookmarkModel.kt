package com.blacksmith.quranApp.data.model

import java.io.Serializable

data class BookmarkModel(
    val surahId: Int,
    val ayah: Int,
    val page: Int,
    val surahName: String,
    val ayaText: String,
    val savedAt: Long = System.currentTimeMillis(),
) : Serializable
