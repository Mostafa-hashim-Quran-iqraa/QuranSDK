package com.blacksmith.quranApp.presentation.quran

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
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



    fun init() {
    }

    fun onDispose() {
    }

    override fun onCleared() {
        super.onCleared()
    }

}
