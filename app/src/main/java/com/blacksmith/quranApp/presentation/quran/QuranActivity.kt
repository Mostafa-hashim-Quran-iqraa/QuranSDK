package com.blacksmith.quranApp.presentation.quran

import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.blacksmith.quranApp.presentation.base.BaseActivity

class QuranActivity :
    BaseActivity(isWhiteActionBar = true, isFullScreen = false, noLimitScreen = false) {
    private val viewModel: QuranViewModel by viewModels()

    @Composable
    override fun DoCreate() {
        viewModel.pageToOpen = intent.getIntExtra("pageToOpen", 0)
        viewModel.isAyaHighlight = intent.getBooleanExtra("isAyaHighlight", false)
        viewModel.isEnableJuzClick = intent.getBooleanExtra("isEnableJuzClick", false)
        viewModel.isEnableSuraClick = intent.getBooleanExtra("isEnableSuraClick", false)
        viewModel.isBoldFont = intent.getBooleanExtra("isBoldFont", true)
        //bg color
        viewModel.bgColor = intent.getStringExtra("selectedBGColor")?: "FDF8F2"
        //font color
        viewModel.fontColor = intent.getStringExtra("selectedFontColor")?: "000000"
        //sura header
        viewModel.surahHeaderColor = intent.getStringExtra("selectedSurahHeaderColor")?: "000000"
        //sura title color
        viewModel.surahTitleColor = intent.getStringExtra("selectedSurahTitleColor")?: "000000"
        //highlight color
        viewModel.highlightColor = intent.getStringExtra("selectedHighlightColor")?: "DBEBF7"
        //ayah number color
        viewModel.ayahNumberColor = intent.getStringExtra("selectedAyahNumberColor")?: "000000"

        Scaffold(
            modifier = Modifier
                .systemBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize(),
        ) {
            it
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(viewModel.bgColor.toColorInt()))
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

