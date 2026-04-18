package com.blacksmith.quranlib.data.model

/**
 * Represents a single Surah entry inside a Juz for the Quran index screen.
 *
 * @param surahId     Surah number (1–114)
 * @param surahNameAr Surah Arabic name (e.g. "الفاتحة")
 * @param firstAyaText First aya text with tashkeel (for preview)
 * @param page        Mushaf page where this surah starts
 */
data class SurahIndexEntry(
    val surahId: Int,
    val surahNameAr: String,
    val firstAyaText: String,
    val page: Int,
)

/**
 * Represents one Juz (part) with the list of Surahs that START in it.
 *
 * @param juzId     Juz number (1–30)
 * @param juzNameAr Juz Arabic name (e.g. "الجزء الأول")
 * @param surahs    Surahs whose first aya falls inside this Juz
 */
data class JuzIndexItem(
    val juzId: Int,
    val juzNameAr: String,
    val surahs: List<SurahIndexEntry>,
)
