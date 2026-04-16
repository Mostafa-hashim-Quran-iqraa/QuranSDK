package com.blacksmith.quranApp.presentation.quran

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.blacksmith.quranApp.presentation.base.BaseViewModel
import com.blacksmith.quranlib.data.util.QuranConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class QuranViewModel @Inject constructor(
) :
    BaseViewModel() {
    var pageToOpen by mutableIntStateOf(0)
    var isAyaHighlight by mutableStateOf(false)
    var isEnableJuzClick by mutableStateOf(false)
    var isEnableSuraClick by mutableStateOf(false)
    var isBoldFont by mutableStateOf(true)
    var bgColor by mutableStateOf("")
    var fontColor by mutableStateOf("")
    var surahHeaderColor by mutableStateOf("")
    var surahTitleColor by mutableStateOf("")
    var highlightColor by mutableStateOf("")

    var ayahNumberColor by mutableStateOf("")

    var isText by mutableStateOf(true)
    var quranPagesVersion by mutableIntStateOf(QuranConstants.PAGES_VERSION_2)

    fun onDispose() {
    }

    override fun onCleared() {
        super.onCleared()
    }

}
