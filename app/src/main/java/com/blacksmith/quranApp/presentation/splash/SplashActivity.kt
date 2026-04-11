package com.blacksmith.quranApp.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
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
import com.blacksmith.quranApp.presentation.base.BaseActivity
import com.blacksmith.quranApp.presentation.base.theme.WhiteSmoke
import com.blacksmith.quranApp.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity :
    BaseActivity(isWhiteActionBar = false, isFullScreen = true, noLimitScreen = true) {

    private val splashViewModel: SplashViewModel by viewModels()

    @Composable
    override fun DoCreate() {
        if (splashViewModel.isNavigateToScreen) {
            when (splashViewModel.navigationScreen) {
                splashViewModel.introToursScreen -> openMainScreen()
            }
            splashViewModel.updateIsNavigateToScreen(false)
        }
        Scaffold(
            modifier = Modifier.Companion
                .systemBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize(),
        ) {
            it
            Surface(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .background(color = WhiteSmoke)
                //remove padding if we hide bottomBar or appbar,
                // to remove its background in all screens that not contains bottomBar
            ) {
                SplashScreen(
                    viewModel = splashViewModel
                )
            }
        }
    }

    fun openMainScreen() {
        Intent(this@SplashActivity, MainActivity::class.java).also {
            startActivity(it)
            finish()
        }
        finish()
    }

}