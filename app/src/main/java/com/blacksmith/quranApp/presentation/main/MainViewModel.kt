package com.blacksmith.quranApp.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.blacksmith.quranApp.data.model.ColorModel
import com.blacksmith.quranApp.data.util.TickFlowState
import com.blacksmith.quranApp.data.util.tickerFlow
import com.blacksmith.quranApp.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
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
    var pageToOpen by mutableIntStateOf(0)
    var isOnlyWordHighlight by mutableStateOf(false)
    var isEnableJuzClick by mutableStateOf(false)
    var isEnableSuraClick by mutableStateOf(false)

    init {
        initBGColorList()
        initSurahHeaderColorList()
        initSurahTitleColorList()
        initFontColorList()
        initHighlightColorList()
    }

    fun initBGColorList() {
        bgColors.clear()
        bgColors.add(ColorModel(colorCode = "#AF8F6E", true))
        bgColors.add(ColorModel(colorCode = "#0073C9"))
        bgColors.add(ColorModel(colorCode = "#1BF802"))
        bgColors.add(ColorModel(colorCode = "#FA050F"))
        bgColors.add(ColorModel(colorCode = "#FDF8F2"))
        bgColors.add(ColorModel(colorCode = "#FFFFFF"))
        bgColors.add(ColorModel(colorCode = "#000000"))
    }

    fun selectOnlyBGColor(position: Int) {
        for (i in bgColors.indices) {
            bgColors[i] = bgColors[i].copy(selected = i == position)
        }
    }

    fun initSurahHeaderColorList() {
        surahHeaderColors.clear()
        surahHeaderColors.add(ColorModel(colorCode = "#AF8F6E", true))
        surahHeaderColors.add(ColorModel(colorCode = "#0073C9"))
        surahHeaderColors.add(ColorModel(colorCode = "#1BF802"))
        surahHeaderColors.add(ColorModel(colorCode = "#FA050F"))
        surahHeaderColors.add(ColorModel(colorCode = "#FDF8F2"))
        surahHeaderColors.add(ColorModel(colorCode = "#FFFFFF"))
        surahHeaderColors.add(ColorModel(colorCode = "#000000"))
    }

    fun selectOnlySurahHeaderColor(position: Int) {
        for (i in surahHeaderColors.indices) {
            surahHeaderColors[i] = surahHeaderColors[i].copy(selected = i == position)
        }
    }

    fun initSurahTitleColorList() {
        surahTitleColors.clear()
        surahTitleColors.add(ColorModel(colorCode = "#AF8F6E", true))
        surahTitleColors.add(ColorModel(colorCode = "#0073C9"))
        surahTitleColors.add(ColorModel(colorCode = "#1BF802"))
        surahTitleColors.add(ColorModel(colorCode = "#FA050F"))
        surahTitleColors.add(ColorModel(colorCode = "#FDF8F2"))
        surahTitleColors.add(ColorModel(colorCode = "#FFFFFF"))
        surahTitleColors.add(ColorModel(colorCode = "#000000"))
    }

    fun selectOnlySurahTitleColor(position: Int) {
        for (i in surahTitleColors.indices) {
            surahTitleColors[i] = surahTitleColors[i].copy(selected = i == position)
        }
    }

    fun initFontColorList() {
        fontColors.clear()
        fontColors.add(ColorModel(colorCode = "#AF8F6E", true))
        fontColors.add(ColorModel(colorCode = "#0073C9"))
        fontColors.add(ColorModel(colorCode = "#1BF802"))
        fontColors.add(ColorModel(colorCode = "#FA050F"))
        fontColors.add(ColorModel(colorCode = "#FDF8F2"))
        fontColors.add(ColorModel(colorCode = "#FFFFFF"))
        fontColors.add(ColorModel(colorCode = "#000000"))
    }

    fun selectOnlyFontColor(position: Int) {
        for (i in fontColors.indices) {
            fontColors[i] = fontColors[i].copy(selected = i == position)
        }
    }

    fun initHighlightColorList() {
        highlightColors.clear()
        highlightColors.add(ColorModel(colorCode = "#55AF8F6E", true))
        highlightColors.add(ColorModel(colorCode = "#550073C9"))
        highlightColors.add(ColorModel(colorCode = "#551BF802"))
        highlightColors.add(ColorModel(colorCode = "#55FA050F"))
        highlightColors.add(ColorModel(colorCode = "#55FDF8F2"))
        highlightColors.add(ColorModel(colorCode = "#55FFFF00"))
        highlightColors.add(ColorModel(colorCode = "#55000000"))
    }

    fun selectOnlyHighlightColor(position: Int) {
        for (i in highlightColors.indices) {
            highlightColors[i] = highlightColors[i].copy(selected = i == position)
        }
    }


    fun onDispose() {
    }

    override fun onCleared() {
        super.onCleared()
    }

}
