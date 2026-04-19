package com.blacksmith.quranApp.presentation.bookmarks

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.blacksmith.quranApp.data.model.BookmarkModel
import com.blacksmith.quranApp.data.util.BookmarkManager
import com.blacksmith.quranApp.presentation.base.BaseViewModel
import com.blacksmith.quranlib.data.model.JuzIndexItem
import com.blacksmith.quranlib.data.model.SurahListItem
import com.blacksmith.quranlib.data.useCase.QuranSearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val quranSearch: QuranSearchUseCase,
) : BaseViewModel() {

    // ── Bookmarks ──────────────────────────────────────────────────────────────
    var bookmarks by mutableStateOf<List<BookmarkModel>>(emptyList())
        private set

    fun loadBookmarks(context: Context) {
        bookmarks = BookmarkManager.getBookmarks(context)
    }

    fun removeBookmark(context: Context, surahId: Int, ayah: Int) {
        BookmarkManager.removeBookmark(context, surahId, ayah)
        bookmarks = BookmarkManager.getBookmarks(context)
    }

    // ── Juz index ──────────────────────────────────────────────────────────────
    var juzList by mutableStateOf<List<JuzIndexItem>>(emptyList())
        private set
    var isLoadingJuz by mutableStateOf(false)
        private set

    fun loadJuzList(context: Context) {
        if (juzList.isNotEmpty()) return
        viewModelScope.launch {
            isLoadingJuz = true
            juzList = try {
                quranSearch.getJuzIndex(context)
            } catch (e: Exception) {
                emptyList()
            }
            isLoadingJuz = false
        }
    }

    // ── Surah list ─────────────────────────────────────────────────────────────
    var surahList by mutableStateOf<List<SurahListItem>>(emptyList())
        private set
    var isLoadingSurahs by mutableStateOf(false)
        private set

    fun loadSurahList(context: Context) {
        if (surahList.isNotEmpty()) return
        viewModelScope.launch {
            isLoadingSurahs = true
            surahList = try {
                quranSearch.getSurahList(context)
            } catch (e: Exception) {
                emptyList()
            }
            isLoadingSurahs = false
        }
    }
}
