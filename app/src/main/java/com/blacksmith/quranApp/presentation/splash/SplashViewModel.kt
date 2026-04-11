package com.blacksmith.quranApp.presentation.splash

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
open class SplashViewModel @Inject constructor(
) :
    BaseViewModel() {
    private var timerTotalPeriod = 2L
    var timerFinished by mutableStateOf(false)
        private set
    val introToursScreen = 0

    var isNavigateToScreen by mutableStateOf(false)
        private set
    var navigationScreen = 0
    var progress by mutableIntStateOf(0)
        private set

    fun updateIsNavigateToScreen(value: Boolean) {
        isNavigateToScreen = value
    }

    fun init() {
        startTimer()
    }

    fun onDispose() {
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    var timerJob: Job? = null
    private fun startTimer() {
        timerFinished = false
        viewModelScope.launch {
            timerJob = launch {
                tickerFlow(0, timerTotalPeriod, 0, 1, TimeUnit.SECONDS).collect {
                    when (it) {
                        is TickFlowState.Tick -> {
                            progress = ((it.tick * 100 / timerTotalPeriod)).toInt()
                        }

                        is TickFlowState.Finish -> {
                            progress = 100
                            timerFinished = true
//                            if (connectionFinished) {
                            toNextScreen()
//                            }
                        }
                    }
                }
            }
        }
    }

    fun toNextScreen() {
        navigationScreen = introToursScreen
        isNavigateToScreen = true
    }
}
