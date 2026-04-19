package com.blacksmith.quranApp.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.blacksmith.quranApp.data.model.ColorModel
import com.blacksmith.quranApp.presentation.base.BaseViewModel
import com.blacksmith.quranlib.data.util.QuranConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class MainViewModel @Inject constructor(
) :
    BaseViewModel() {
    var bgColors = mutableStateListOf<ColorModel>()
    var surahHeaderColors = mutableStateListOf<ColorModel>()
    var surahTitleColors = mutableStateListOf<ColorModel>()
    var fontColors = mutableStateListOf<ColorModel>()
    var highlightColors = mutableStateListOf<ColorModel>()
    var bookmarkHighlightColors = mutableStateListOf<ColorModel>()
    var ayaNumberColors = mutableStateListOf<ColorModel>()
    var pageToOpen by mutableIntStateOf(1)
    var isAyaHighlight by mutableStateOf(true)
    var isBookmarkAyaHighlight by mutableStateOf(true)
    var isEnableJuzClick by mutableStateOf(false)
    var isEnableSuraClick by mutableStateOf(false)
    var isBoldFont by mutableStateOf(true)
    var quranPagesVersion by mutableIntStateOf(QuranConstants.VERSION_KING_FAHD_1421)

    init {
        initBGColorList()
        initSurahHeaderColorList()
        initSurahTitleColorList()
        initFontColorList()
        initHighlightColorList()
        initBookmarkHighlightColorList()
        ayaNumberColorList()
    }

    fun initBGColorList() {
        bgColors.clear()
        bgColors.add(ColorModel(colorCode = "#FFFFFF",true))
        bgColors.add(ColorModel(colorCode = "#AF8F6E", ))
        bgColors.add(ColorModel(colorCode = "#0073C9"))
        bgColors.add(ColorModel(colorCode = "#1BF802"))
        bgColors.add(ColorModel(colorCode = "#FA050F"))
        bgColors.add(ColorModel(colorCode = "#FDF8F2"))
        bgColors.add(ColorModel(colorCode = "#000000"))
        bgColors.add(ColorModel(colorCode = "#14352A"))
    }

    fun selectOnlyBGColor(position: Int) {
        for (i in bgColors.indices) {
            bgColors[i] = bgColors[i].copy(selected = i == position)
        }
    }

    fun initSurahHeaderColorList() {
        surahHeaderColors.clear()
        surahHeaderColors.add(ColorModel(colorCode = "#000000",true))
        surahHeaderColors.add(ColorModel(colorCode = "#AF8F6E", ))
        surahHeaderColors.add(ColorModel(colorCode = "#0073C9"))
        surahHeaderColors.add(ColorModel(colorCode = "#1BF802"))
        surahHeaderColors.add(ColorModel(colorCode = "#FA050F"))
        surahHeaderColors.add(ColorModel(colorCode = "#FDF8F2"))
        surahHeaderColors.add(ColorModel(colorCode = "#FFFFFF"))
        surahHeaderColors.add(ColorModel(colorCode = "#14352A"))

    }

    fun selectOnlySurahHeaderColor(position: Int) {
        for (i in surahHeaderColors.indices) {
            surahHeaderColors[i] = surahHeaderColors[i].copy(selected = i == position)
        }
    }

    fun initSurahTitleColorList() {
        surahTitleColors.clear()
        surahTitleColors.add(ColorModel(colorCode = "#0073C9",true))
        surahTitleColors.add(ColorModel(colorCode = "#AF8F6E", ))
        surahTitleColors.add(ColorModel(colorCode = "#1BF802"))
        surahTitleColors.add(ColorModel(colorCode = "#FA050F"))
        surahTitleColors.add(ColorModel(colorCode = "#FDF8F2"))
        surahTitleColors.add(ColorModel(colorCode = "#FFFFFF"))
        surahTitleColors.add(ColorModel(colorCode = "#000000"))
        surahTitleColors.add(ColorModel(colorCode = "#14352A"))
    }

    fun selectOnlySurahTitleColor(position: Int) {
        for (i in surahTitleColors.indices) {
            surahTitleColors[i] = surahTitleColors[i].copy(selected = i == position)
        }
    }

    fun initFontColorList() {
        fontColors.clear()
        fontColors.add(ColorModel(colorCode = "#000000",true))
        fontColors.add(ColorModel(colorCode = "#AF8F6E", ))
        fontColors.add(ColorModel(colorCode = "#0073C9"))
        fontColors.add(ColorModel(colorCode = "#1BF802"))
        fontColors.add(ColorModel(colorCode = "#FA050F"))
        fontColors.add(ColorModel(colorCode = "#FDF8F2"))
        fontColors.add(ColorModel(colorCode = "#FFFFFF"))
        fontColors.add(ColorModel(colorCode = "#14352A"))

    }

    fun selectOnlyFontColor(position: Int) {
        for (i in fontColors.indices) {
            fontColors[i] = fontColors[i].copy(selected = i == position)
        }
    }

    fun initHighlightColorList() {
        highlightColors.clear()
        highlightColors.add(ColorModel(colorCode = "#550073C9",true))
        highlightColors.add(ColorModel(colorCode = "#55AF8F6E", ))
        highlightColors.add(ColorModel(colorCode = "#551BF802"))
        highlightColors.add(ColorModel(colorCode = "#55FA050F"))
        highlightColors.add(ColorModel(colorCode = "#55FDF8F2"))
        highlightColors.add(ColorModel(colorCode = "#55FFFF00"))
        highlightColors.add(ColorModel(colorCode = "#55000000"))
        highlightColors.add(ColorModel(colorCode = "#14352A"))
    }

    fun initBookmarkHighlightColorList() {
        bookmarkHighlightColors.clear()
        bookmarkHighlightColors.add(ColorModel(colorCode = "#550073C9",true))
        bookmarkHighlightColors.add(ColorModel(colorCode = "#55AF8F6E", ))
        bookmarkHighlightColors.add(ColorModel(colorCode = "#551BF802"))
        bookmarkHighlightColors.add(ColorModel(colorCode = "#55FA050F"))
        bookmarkHighlightColors.add(ColorModel(colorCode = "#55FDF8F2"))
        bookmarkHighlightColors.add(ColorModel(colorCode = "#55FFFF00"))
        bookmarkHighlightColors.add(ColorModel(colorCode = "#55000000"))
        bookmarkHighlightColors.add(ColorModel(colorCode = "#14352A"))
    }

    fun ayaNumberColorList() {
        ayaNumberColors.clear()
        ayaNumberColors.add(ColorModel(colorCode = "#000000",true))
        ayaNumberColors.add(ColorModel(colorCode = "#AF8F6E", ))
        ayaNumberColors.add(ColorModel(colorCode = "#0073C9"))
        ayaNumberColors.add(ColorModel(colorCode = "#1BF802"))
        ayaNumberColors.add(ColorModel(colorCode = "#FA050F"))
        ayaNumberColors.add(ColorModel(colorCode = "#FDF8F2"))
        ayaNumberColors.add(ColorModel(colorCode = "#FFFFFF"))
        ayaNumberColors.add(ColorModel(colorCode = "#14352A"))
    }

    fun selectOnlyHighlightColor(position: Int) {
        for (i in highlightColors.indices) {
            highlightColors[i] = highlightColors[i].copy(selected = i == position)
        }
    }

    fun selectOnlyAyaNumberColor(position: Int) {
        for (i in ayaNumberColors.indices) {
            ayaNumberColors[i] = ayaNumberColors[i].copy(selected = i == position)
        }
    }


    fun onDispose() {
    }

    override fun onCleared() {
        super.onCleared()
    }

}
