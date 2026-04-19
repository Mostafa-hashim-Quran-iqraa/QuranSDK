package com.blacksmith.quranApp.presentation.bookmarks

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.blacksmith.quranApp.data.model.BookmarkModel
import com.blacksmith.quranApp.data.util.BookmarkManager
import com.blacksmith.quranApp.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor() : BaseViewModel() {

    var bookmarks by mutableStateOf<List<BookmarkModel>>(emptyList())
        private set

    fun loadBookmarks(context: Context) {
        bookmarks = BookmarkManager.getBookmarks(context)
    }

    fun removeBookmark(context: Context, surahId: Int, ayah: Int) {
        BookmarkManager.removeBookmark(context, surahId, ayah)
        bookmarks = BookmarkManager.getBookmarks(context)
    }
}
