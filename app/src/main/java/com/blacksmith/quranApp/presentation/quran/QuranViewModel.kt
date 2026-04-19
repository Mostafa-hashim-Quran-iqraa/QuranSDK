package com.blacksmith.quranApp.presentation.quran

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.blacksmith.quranApp.presentation.base.BaseViewModel
import com.blacksmith.quranlib.data.model.AyaModel
import com.blacksmith.quranlib.data.model.JuzIndexItem
import com.blacksmith.quranlib.data.util.QuranConstants
import com.blacksmith.quranlib.data.useCase.QuranSearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class QuranViewModel @Inject constructor(
    private val quranSearch: QuranSearchUseCase,
) : BaseViewModel() {

    var pageToOpen by mutableIntStateOf(0)
    var highlightType by mutableIntStateOf(QuranConstants.HIGHLIGHT_TYPE_AYA)
    var isEnableJuzClick by mutableStateOf(false)
    var isEnableSuraClick by mutableStateOf(false)
    var isBoldFont by mutableStateOf(true)
    var bgColor by mutableStateOf("")
    var fontColor by mutableStateOf("")
    var surahHeaderColor by mutableStateOf("")
    var surahTitleColor by mutableStateOf("")
    var highlightColor by mutableStateOf("")
    var bookmarkHighlightColor by mutableStateOf("")
    var ayahNumberColor by mutableStateOf("")
    var quranPagesVersion by mutableIntStateOf(QuranConstants.VERSION_KING_FAHD_1421)

    // ─── Search state ─────────────────────────────────────────────────────────
    var isSearchVisible by mutableStateOf(false)
        private set
    var searchQuery by mutableStateOf("")
    var searchResults by mutableStateOf<List<AyaModel>>(emptyList())
        private set
    var isSearchLoading by mutableStateOf(false)
        private set

    fun showSearch() { isSearchVisible = true }

    fun hideSearch() {
        isSearchVisible = false
        searchQuery = ""
        searchResults = emptyList()
    }

    fun clearSearchResults() { searchResults = emptyList() }

    fun searchAyas(context: Context, query: String) {
        viewModelScope.launch {
            isSearchLoading = true
            searchResults = try {
                quranSearch.searchAyas(context, query)
            } catch (e: Exception) {
                emptyList()
            }
            isSearchLoading = false
        }
    }

    // ─── Juz index state ─────────────────────────────────────────────────────
    var isIndexVisible by mutableStateOf(false)
        private set
    var juzIndex by mutableStateOf<List<JuzIndexItem>>(emptyList())
        private set
    var selectedJuzId by mutableIntStateOf(1)

    fun showIndex(context: Context) {
        isIndexVisible = true
        if (juzIndex.isEmpty()) {
            viewModelScope.launch {
                juzIndex = try {
                    quranSearch.getJuzIndex(context)
                } catch (e: Exception) {
                    emptyList()
                }
            }
        }
    }

    fun hideIndex() { isIndexVisible = false }

    fun onDispose() {}

    override fun onCleared() {
        super.onCleared()
    }
}
