package com.blacksmith.quranApp.presentation.quran

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import com.blacksmith.quranApp.R
import com.blacksmith.quranApp.presentation.base.theme.White
import com.blacksmith.quranlib.data.util.component.ComposableLifecycle
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranApp.data.util.component.LoaderLottie
import com.blacksmith.quranApp.presentation.base.theme.Black
import com.blacksmith.quranApp.presentation.base.theme.colorPrimary
import com.blacksmith.quranApp.presentation.base.theme.colorPrimaryDark
import com.blacksmith.quranApp.presentation.base.theme.colorPrimaryMoreLight
import com.blacksmith.quranApp.presentation.base.theme.gray_400
import com.blacksmith.quranApp.presentation.base.theme.krema
import com.blacksmith.quranApp.presentation.base.theme.red_light
import com.blacksmith.quranApp.presentation.main.MainViewModel
import com.blacksmith.quranlib.data.util.helper.toSP
import com.blacksmith.quranlib.presentation.quranScreen.QuranPageScreen

@Composable
fun QuranScreen(
    viewModel: QuranViewModel
) {
    val context = LocalContext.current

    ComposableLifecycle { source, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
            }

            Lifecycle.Event.ON_START -> {
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
fun Content(context: Context = LocalContext.current, viewModel: QuranViewModel) {
    val context = LocalContext.current
    Surface(
        color = Color(viewModel.bgColor.toColorInt()), modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.toDP),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.toDP),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Icon(
                    modifier = Modifier
                        .width(30.toDP)
                        .height(30.toDP)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = gray_400),
                            onClick = {
                                (context as QuranActivity).finish()
                            }
                        )
                        .padding(7.toDP),
                    tint = Color(viewModel.fontColor.toColorInt()),
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "",
                )
                Text(
                    text = stringResource(R.string.quran),
                    modifier = Modifier.padding(start = 10.toDP),
                    color = Color(viewModel.fontColor.toColorInt()),
                    fontSize = 16.toSP,
                )
            }

            QuranPageScreen(
                isReversePager = !viewModel.isArabicLocale(),
                fontColor = Color(viewModel.fontColor.toColorInt()),
                suraHeaderColor = Color(viewModel.surahHeaderColor.toColorInt()),
                suraNameColor = Color(viewModel.surahTitleColor.toColorInt()),
                highlightColor = Color(viewModel.highlightColor.toColorInt()),
                pageBackground = Color(viewModel.bgColor.toColorInt()),
            )
        }
    }
}
