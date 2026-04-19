package com.blacksmith.quranApp.data.model

import java.io.Serializable

/**
 * Represents a word that has been flagged as containing an error.
 *
 * @param location  Word location string (e.g. "2:5:3") — matches [WordModel.location]
 * @param wordText  Plain Arabic text of the word
 * @param surahId   Surah number (1–114)
 * @param surahName Arabic surah name
 * @param ayah      Aya number within the surah
 * @param page      Mushaf page where this word appears
 * @param savedAt   Epoch ms when the error was flagged
 */
data class WordErrorModel(
    val location: String,
    val wordText: String,
    val surahId: Int,
    val surahName: String,
    val ayah: Int,
    val page: Int,
    val savedAt: Long = System.currentTimeMillis(),
) : Serializable
