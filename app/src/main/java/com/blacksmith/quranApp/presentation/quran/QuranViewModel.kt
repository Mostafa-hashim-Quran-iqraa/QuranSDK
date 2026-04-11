package com.blacksmith.quranApp.presentation.quran

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
open class QuranViewModel @Inject constructor(
) :
    BaseViewModel() {
    var pageToOpen by mutableIntStateOf(0)
    var isOnlyWordHighlight by mutableStateOf(false)
    var isEnableJuzClick by mutableStateOf(false)
    var isEnableSuraClick by mutableStateOf(false)
    var bgColor by mutableStateOf("")
    var fontColor by mutableStateOf("")
    var surahHeaderColor by mutableStateOf("")
    var surahTitleColor by mutableStateOf("")
    var highlightColor by mutableStateOf("")


    fun onDispose() {
    }

    override fun onCleared() {
        super.onCleared()
    }

}
