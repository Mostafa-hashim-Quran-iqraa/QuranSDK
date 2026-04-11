package com.blacksmith.quranApp.presentation.main

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.blacksmith.quranApp.presentation.base.theme.colorPrimary
import com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular600
import com.blacksmith.quranApp.presentation.base.theme.gray_200
import com.blacksmith.quranApp.presentation.base.theme.gray_400
import com.blacksmith.quranApp.presentation.base.theme.transparent
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
                .padding(16.toDP),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            //page bg
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.toDP),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            )
            {
                Text(
                    text = stringResource(R.string.page_background_color),
                    fontSize = 14.toSP,
                    color = colorPrimary,
                    fontFamily = fontNeoSansArabicRegular600
                )

                LazyRow(
                    modifier = Modifier.padding(top = 5.toDP),
                    horizontalArrangement = Arrangement.spacedBy(10.toDP),
                ) {
                    items(count = viewModel.bgColors.size) { pos ->
                        Box(
                            modifier = Modifier
                                .padding(0.toDP)
                                .size(40.toDP)
                                .clip(RoundedCornerShape(8.toDP))
                                .border(
                                    width = if (viewModel.bgColors[pos].selected) 2.toDP else 1.toDP,
                                    color = if (viewModel.bgColors[pos].selected) colorPrimary else gray_200,
                                    shape = RoundedCornerShape(8.toDP)
                                )
                                .clickable(
                                    onClick = {
                                        viewModel.selectOnlyBGColor(pos)
                                    }
                                )
                                .background(transparent)
                                .padding(4.toDP),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.toDP))
                                    .background(
                                        Color(viewModel.bgColors[pos].colorCode.toColorInt())
                                    )
                            )
                        }
                    }
                }

            }
            //font color
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.toDP),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            )
            {
                Text(
                    text = stringResource(R.string.font_color),
                    fontSize = 14.toSP,
                    color = colorPrimary,
                    fontFamily = fontNeoSansArabicRegular600
                )

                LazyRow(
                    modifier = Modifier.padding(top = 5.toDP),
                    horizontalArrangement = Arrangement.spacedBy(10.toDP),
                ) {
                    items(count = viewModel.fontColors.size) { pos ->
                        Box(
                            modifier = Modifier
                                .padding(0.toDP)
                                .size(40.toDP)
                                .clip(RoundedCornerShape(8.toDP))
                                .border(
                                    width = if (viewModel.fontColors[pos].selected) 2.toDP else 1.toDP,
                                    color = if (viewModel.fontColors[pos].selected) colorPrimary else gray_200,
                                    shape = RoundedCornerShape(8.toDP)
                                )
                                .clickable(
                                    onClick = {
                                        viewModel.selectOnlyFontColor(pos)
                                    }
                                )
                                .background(transparent)
                                .padding(4.toDP),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.toDP))
                                    .background(
                                        Color(viewModel.fontColors[pos].colorCode.toColorInt())
                                    )
                            )
                        }
                    }
                }

            }

            //surah header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.toDP),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            )
            {
                Text(
                    text = stringResource(R.string.sura_header_color),
                    fontSize = 14.toSP,
                    color = colorPrimary,
                    fontFamily = fontNeoSansArabicRegular600
                )

                LazyRow(
                    modifier = Modifier.padding(top = 5.toDP),
                    horizontalArrangement = Arrangement.spacedBy(10.toDP),
                ) {
                    items(count = viewModel.surahHeaderColors.size) { pos ->
                        Box(
                            modifier = Modifier
                                .padding(0.toDP)
                                .size(40.toDP)
                                .clip(RoundedCornerShape(8.toDP))
                                .border(
                                    width = if (viewModel.surahHeaderColors[pos].selected) 2.toDP else 1.toDP,
                                    color = if (viewModel.surahHeaderColors[pos].selected) colorPrimary else gray_200,
                                    shape = RoundedCornerShape(8.toDP)
                                )
                                .clickable(
                                    onClick = {
                                        viewModel.selectOnlySurahHeaderColor(pos)
                                    }
                                )
                                .background(transparent)
                                .padding(4.toDP),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.toDP))
                                    .background(
                                        Color(viewModel.surahHeaderColors[pos].colorCode.toColorInt())
                                    )
                            )
                        }
                    }
                }

            }

            //sura title color
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.toDP),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            )
            {
                Text(
                    text = stringResource(R.string.sura_title_color),
                    fontSize = 14.toSP,
                    color = colorPrimary,
                    fontFamily = fontNeoSansArabicRegular600
                )

                LazyRow(
                    modifier = Modifier.padding(top = 5.toDP),
                    horizontalArrangement = Arrangement.spacedBy(10.toDP),
                ) {
                    items(count = viewModel.surahTitleColors.size) { pos ->
                        Box(
                            modifier = Modifier
                                .padding(0.toDP)
                                .size(40.toDP)
                                .clip(RoundedCornerShape(8.toDP))
                                .border(
                                    width = if (viewModel.surahTitleColors[pos].selected) 2.toDP else 1.toDP,
                                    color = if (viewModel.surahTitleColors[pos].selected) colorPrimary else gray_200,
                                    shape = RoundedCornerShape(8.toDP)
                                )
                                .clickable(
                                    onClick = {
                                        viewModel.selectOnlySurahTitleColor(pos)
                                    }
                                )
                                .background(transparent)
                                .padding(4.toDP),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.toDP))
                                    .background(
                                        Color(viewModel.surahTitleColors[pos].colorCode.toColorInt())
                                    )
                            )
                        }
                    }
                }

            }

            //highlight color
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.toDP),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            )
            {
                Text(
                    text = stringResource(R.string.highlight_color),
                    fontSize = 14.toSP,
                    color = colorPrimary,
                    fontFamily = fontNeoSansArabicRegular600
                )

                LazyRow(
                    modifier = Modifier.padding(top = 5.toDP),
                    horizontalArrangement = Arrangement.spacedBy(10.toDP),
                ) {
                    items(count = viewModel.highlightColors.size) { pos ->
                        Box(
                            modifier = Modifier
                                .padding(0.toDP)
                                .size(40.toDP)
                                .clip(RoundedCornerShape(8.toDP))
                                .border(
                                    width = if (viewModel.highlightColors[pos].selected) 2.toDP else 1.toDP,
                                    color = if (viewModel.highlightColors[pos].selected) colorPrimary else gray_200,
                                    shape = RoundedCornerShape(8.toDP)
                                )
                                .clickable(
                                    onClick = {
                                        viewModel.selectOnlyHighlightColor(pos)
                                    }
                                )
                                .background(transparent)
                                .padding(4.toDP),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.toDP))
                                    .background(
                                        Color(viewModel.highlightColors[pos].colorCode.toColorInt())
                                    )
                            )
                        }
                    }
                }

            }

            Box(
                modifier = Modifier
                    .padding(top = 20.toDP)
                    .fillMaxWidth()
                    .height(50.toDP)
                    .clip(RoundedCornerShape(16.toDP))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = gray_400),
                        onClick = {
                            (context as MainActivity).startActivity(
                                Intent(
                                    context,
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
                    fontSize = 14.toSP,
                    color = White,
                    fontFamily = fontNeoSansArabicRegular600
                )
            }
        }
    }
}
