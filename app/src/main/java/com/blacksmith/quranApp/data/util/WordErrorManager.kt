package com.blacksmith.quranApp.data.util

import android.content.Context
import com.blacksmith.quranApp.data.model.WordErrorModel
import org.json.JSONArray
import org.json.JSONObject

/**
 * SharedPreferences-backed store for words flagged as errors.
 * Uses org.json so no Gson / Moshi dependency is needed.
 */
object WordErrorManager {

    private const val PREFS_NAME = "quran_word_errors"
    private const val KEY_ERRORS  = "errors"

    fun getErrorWords(context: Context): List<WordErrorModel> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json  = prefs.getString(KEY_ERRORS, "[]") ?: "[]"
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                WordErrorModel(
                    location  = obj.getString("location"),
                    wordText  = obj.getString("wordText"),
                    surahId   = obj.getInt("surahId"),
                    surahName = obj.getString("surahName"),
                    ayah      = obj.getInt("ayah"),
                    page      = obj.getInt("page"),
                    savedAt   = obj.optLong("savedAt", System.currentTimeMillis()),
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveErrorWord(context: Context, word: WordErrorModel) {
        val current = getErrorWords(context).toMutableList()
        if (current.none { it.location == word.location }) {
            current.add(word)
            persist(context, current)
        }
    }

    fun removeErrorWord(context: Context, location: String) {
        val current = getErrorWords(context).filter { it.location != location }
        persist(context, current)
    }

    fun isErrorWord(context: Context, location: String): Boolean =
        getErrorWords(context).any { it.location == location }

    /** Returns just the location strings — useful for passing to the canvas. */
    fun getLocations(context: Context): Set<String> =
        getErrorWords(context).mapTo(HashSet()) { it.location }

    // ─────────────────────────────────────────────────────────────────────────

    private fun persist(context: Context, list: List<WordErrorModel>) {
        val array = JSONArray()
        list.forEach { w ->
            val obj = JSONObject().apply {
                put("location",  w.location)
                put("wordText",  w.wordText)
                put("surahId",   w.surahId)
                put("surahName", w.surahName)
                put("ayah",      w.ayah)
                put("page",      w.page)
                put("savedAt",   w.savedAt)
            }
            array.put(obj)
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_ERRORS, array.toString()).apply()
    }
}
