package com.blacksmith.quranApp.presentation.quran

import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.blacksmith.quranApp.presentation.base.BaseActivity
import com.blacksmith.quranApp.presentation.base.theme.WhiteSmoke
import com.blacksmith.quranApp.presentation.base.theme.krema
import com.blacksmith.quranApp.presentation.main.MainHomeScreen
import com.blacksmith.quranApp.presentation.main.MainViewModel

class QuranActivity :
    BaseActivity(isWhiteActionBar = true, isFullScreen = false, noLimitScreen = false) {
    private val viewModel: QuranViewModel by viewModels()

    @Composable
    override fun DoCreate() {
        Scaffold(
            modifier = Modifier
                .systemBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize(),
        ) {
            it
            Surface(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .background(color = krema)
                //remove padding if we hide bottomBar or appbar,
                // to remove its background in all screens that not contains bottomBar
            ) {
                QuranScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}

