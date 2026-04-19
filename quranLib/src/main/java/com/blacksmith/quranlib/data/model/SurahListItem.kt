package com.blacksmith.quranlib.data.model

/**
 * A lightweight Surah entry used for the Quran surah index screen.
 *
 * @param surahId     Surah number (1–114)
 * @param surahNameAr Surah Arabic name (e.g. "الفاتحة")
 * @param ayaCount    Total number of verses in this surah
 * @param page        Mushaf page where this surah starts
 */
data class SurahListItem(
    val surahId: Int,
    val surahNameAr: String,
    val ayaCount: Int,
    val page: Int,
)
