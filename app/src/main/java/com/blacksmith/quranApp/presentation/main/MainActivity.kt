package com.blacksmith.quranApp.presentation.main

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

class MainActivity : BaseActivity(isWhiteActionBar = true, isFullScreen = false, noLimitScreen = false)  {
    private val viewModel: MainViewModel by viewModels()

    @Composable
    override fun DoCreate() {
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
                MainHomeScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
}