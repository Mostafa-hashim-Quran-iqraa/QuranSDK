package com.blacksmith.quranApp.data.util

import android.content.Context
import com.blacksmith.quranApp.data.model.BookmarkModel
import org.json.JSONArray
import org.json.JSONObject

object BookmarkManager {

    private const val PREFS_NAME = "quran_bookmarks_prefs"
    private const val KEY_BOOKMARKS = "bookmarks_json"

    // ─── Read ─────────────────────────────────────────────────────────────────
    fun getBookmarks(context: Context): List<BookmarkModel> {
        val json = prefs(context).getString(KEY_BOOKMARKS, "[]") ?: "[]"
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val o = array.getJSONObject(i)
                BookmarkModel(
                    surahId   = o.getInt("surahId"),
                    ayah      = o.getInt("ayah"),
                    page      = o.getInt("page"),
                    surahName = o.getString("surahName"),
                    ayaText   = o.getString("ayaText"),
                    savedAt   = o.optLong("savedAt", System.currentTimeMillis()),
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun isBookmarked(context: Context, surahId: Int, ayah: Int): Boolean =
        getBookmarks(context).any { it.surahId == surahId && it.ayah == ayah }

    // ─── Write ────────────────────────────────────────────────────────────────
    fun saveBookmark(context: Context, bookmark: BookmarkModel) {
        val list = getBookmarks(context).toMutableList()
        // Remove existing entry for the same aya to avoid duplicates
        list.removeAll { it.surahId == bookmark.surahId && it.ayah == bookmark.ayah }
        list.add(bookmark)
        persist(context, list)
    }

    fun removeBookmark(context: Context, surahId: Int, ayah: Int) {
        val list = getBookmarks(context).toMutableList()
        list.removeAll { it.surahId == surahId && it.ayah == ayah }
        persist(context, list)
    }

    // ─── Internal ─────────────────────────────────────────────────────────────
    private fun persist(context: Context, list: List<BookmarkModel>) {
        val array = JSONArray()
        list.forEach { bm ->
            array.put(
                JSONObject().apply {
                    put("surahId",   bm.surahId)
                    put("ayah",      bm.ayah)
                    put("page",      bm.page)
                    put("surahName", bm.surahName)
                    put("ayaText",   bm.ayaText)
                    put("savedAt",   bm.savedAt)
                }
            )
        }
        prefs(context).edit().putString(KEY_BOOKMARKS, array.toString()).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
