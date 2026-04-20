package com.blacksmith.quranApp.presentation.bookmarks

import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.blacksmith.quranApp.presentation.base.BaseActivity
import com.blacksmith.quranApp.presentation.base.theme.White
import com.blacksmith.quranApp.presentation.quran.QuranActivity
import com.blacksmith.quranlib.data.util.QuranConstants
import com.blacksmith.quranlib.presentation.quranScreen.QuranDataGuard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarksActivity :
    BaseActivity(isWhiteActionBar = true, isFullScreen = false, noLimitScreen = false) {

    private val viewModel: BookmarksViewModel by viewModels()

    @Composable
    override fun DoCreate() {
        // Receive the same color config as QuranActivity so we can pass it on
        val bookmarkHighlightColor = intent.getStringExtra("selectedBookmarkHighlightColor") ?: "550073C9"
        val errorHighlightColor    = intent.getStringExtra("selectedErrorHighlightColor")    ?: "#FFE53935"
        val bgColor                = intent.getStringExtra("selectedBGColor")                ?: "FDF8F2"
        val fontColor              = intent.getStringExtra("selectedFontColor")              ?: "000000"
        val surahHeaderColor       = intent.getStringExtra("selectedSurahHeaderColor")       ?: "000000"
        val surahTitleColor        = intent.getStringExtra("selectedSurahTitleColor")        ?: "000000"
        val highlightColor         = intent.getStringExtra("selectedHighlightColor")         ?: "DBEBF7"
        val ayahNumberColor        = intent.getStringExtra("selectedAyahNumberColor")        ?: "000000"
        val isBoldFont             = intent.getBooleanExtra("isBoldFont",           true)
        val quranPagesVersion      = intent.getIntExtra("quranPagesVersion",         QuranConstants.VERSION_KING_FAHD_1441)
        val highlightType          = intent.getIntExtra("highlightType",             QuranConstants.HIGHLIGHT_TYPE_AYA)
        val isEnableJuzClick       = intent.getBooleanExtra("isEnableJuzClick",      false)
        val isEnableSuraClick      = intent.getBooleanExtra("isEnableSuraClick",     false)

        Surface(color = White, modifier = Modifier.fillMaxSize()) {
            QuranDataGuard(versionNumber = quranPagesVersion, needsFonts = false) {
            BookmarksScreen(
                viewModel = viewModel,
                onNavigateTo = { page, ayaNumber, surahId ->
                    startActivity(
                        Intent(this, QuranActivity::class.java).apply {
                            putExtra("pageToOpen",                    page)
                            putExtra("highlightType",                 highlightType)
                            putExtra("isEnableJuzClick",              isEnableJuzClick)
                            putExtra("isEnableSuraClick",             isEnableSuraClick)
                            putExtra("isBoldFont",                    isBoldFont)
                            putExtra("quranPagesVersion",             quranPagesVersion)
                            putExtra("selectedBGColor",               bgColor)
                            putExtra("selectedFontColor",             fontColor)
                            putExtra("selectedSurahHeaderColor",      surahHeaderColor)
                            putExtra("selectedSurahTitleColor",       surahTitleColor)
                            putExtra("selectedHighlightColor",        highlightColor)
                            putExtra("selectedAyahNumberColor",       ayahNumberColor)
                            putExtra("selectedBookmarkHighlightColor", bookmarkHighlightColor)
                            putExtra("selectedErrorHighlightColor",   errorHighlightColor)
                            if (ayaNumber != -1) putExtra("ayaNumberInSuraToHighlight", ayaNumber)
                            if (surahId   != -1) putExtra("surahIdToHighlight",          surahId)
                        }
                    )
                },
            )
            } // QuranDataGuard
        }
    }
}
