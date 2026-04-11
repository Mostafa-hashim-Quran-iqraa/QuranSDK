package com.blacksmith.quranApp.presentation.main

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import com.blacksmith.quranApp.R
import com.blacksmith.quranApp.presentation.base.theme.White
import com.blacksmith.quranlib.data.util.component.ComposableLifecycle
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranApp.data.util.component.LoaderLottie
import com.blacksmith.quranApp.presentation.base.theme.colorPrimary
import com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular600
import com.blacksmith.quranApp.presentation.base.theme.gray_400
import com.blacksmith.quranApp.presentation.quran.QuranActivity
import com.blacksmith.quranlib.data.util.helper.toSP

@Composable
fun MainHomeScreen(
    viewModel: MainViewModel
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
fun Content(context: Context = LocalContext.current, viewModel: MainViewModel) {
    Surface(
        color = White, modifier = Modifier.fillMaxSize()
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
                modifier = Modifier
                    .width(200.toDP)
                    .height(250.toDP),
            )
            Box(
                modifier = Modifier
                    .padding(top = 10.toDP)
                    .fillMaxWidth()
                    .height(50.toDP)
                    .clip(RoundedCornerShape(16.toDP))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = gray_400),
                        onClick = {
                            (context as MainActivity).startActivity(
                                Intent(
                                    (context as MainActivity),
                                    QuranActivity::class.java
                                )
                            )
                        }
                    )
                    .background(colorPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.open_quran_page),
                    fontSize = 16.toSP,
                    fontFamily = fontNeoSansArabicRegular600
                )
            }
        }
    }
}
