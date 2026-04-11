package com.blacksmith.quranApp.presentation.splash

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import com.blacksmith.quranApp.R
import com.blacksmith.quranApp.presentation.base.theme.White
import com.blacksmith.quranApp.presentation.base.theme.colorPrimary
import com.blacksmith.quranlib.data.util.component.ComposableLifecycle
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranApp.data.util.component.LoaderLottie
import com.blacksmith.quranApp.presentation.base.theme.brown
import com.blacksmith.quranApp.presentation.base.theme.krema

@Composable
fun SplashScreen(
    viewModel: SplashViewModel
) {
    val context = LocalContext.current

    ComposableLifecycle { source, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
            }

            Lifecycle.Event.ON_START -> {
                viewModel.init()
            }

            Lifecycle.Event.ON_RESUME -> {
            }

            Lifecycle.Event.ON_PAUSE -> {
            }

            Lifecycle.Event.ON_STOP -> {
                viewModel.onDispose()
            }

            Lifecycle.Event.ON_DESTROY -> {
            }

            else -> {}
        }
    }
    Content(context, viewModel)

}

@Composable
fun Content(context: Context = LocalContext.current, viewModel: SplashViewModel) {
    Surface(
        color = krema, modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.toDP),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.width(200.toDP)
                    .height(250.toDP),
            )
            LoaderLottie(R.raw.loader_circle, brown, 40, 40)
        }
    }
}
