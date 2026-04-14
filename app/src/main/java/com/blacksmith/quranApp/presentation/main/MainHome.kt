package com.blacksmith.quranApp.presentation.main

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import com.blacksmith.quranApp.R
import com.blacksmith.quranApp.data.util.component.CustomTextField
import com.blacksmith.quranApp.presentation.base.theme.White
import com.blacksmith.quranlib.data.util.component.ComposableLifecycle
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranApp.presentation.base.theme.colorPrimary
import com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular400
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
                .verticalScroll(rememberScrollState())
                .imePadding()
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

            //aya number color
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.toDP),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            )
            {
                Text(
                    text = stringResource(R.string.aya_number_color),
                    fontSize = 14.toSP,
                    color = colorPrimary,
                    fontFamily = fontNeoSansArabicRegular600
                )

                LazyRow(
                    modifier = Modifier.padding(top = 5.toDP),
                    horizontalArrangement = Arrangement.spacedBy(10.toDP),
                ) {
                    items(count = viewModel.ayaNumberColors.size) { pos ->
                        Box(
                            modifier = Modifier
                                .padding(0.toDP)
                                .size(40.toDP)
                                .clip(RoundedCornerShape(8.toDP))
                                .border(
                                    width = if (viewModel.ayaNumberColors[pos].selected) 2.toDP else 1.toDP,
                                    color = if (viewModel.ayaNumberColors[pos].selected) colorPrimary else gray_200,
                                    shape = RoundedCornerShape(8.toDP)
                                )
                                .clickable(
                                    onClick = {
                                        viewModel.selectOnlyAyaNumberColor(pos)
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
                                        Color(viewModel.ayaNumberColors[pos].colorCode.toColorInt())
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

            //select aya or word
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.toDP),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            )
            {
                Text(
                    text = stringResource(R.string.highlight_word_or_aya),
                    fontSize = 14.toSP,
                    color = colorPrimary,
                    fontFamily = fontNeoSansArabicRegular600
                )

                Row(
                    modifier = Modifier.padding(top = 5.toDP),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(0.toDP)
                            .weight(1f)
                            .clip(RoundedCornerShape(8.toDP))
                            .border(
                                width = 1.toDP,
                                color = if (viewModel.isAyaHighlight) colorPrimary else gray_200,
                                shape = RoundedCornerShape(8.toDP)
                            )
                            .clickable(
                                onClick = {
                                    viewModel.isAyaHighlight = true
                                }
                            )
                            .background(if (viewModel.isAyaHighlight) colorPrimary else White)
                            .padding(4.toDP),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.highlight_aya),
                            fontSize = 14.toSP,
                            color = if (viewModel.isAyaHighlight) White else colorPrimary,
                            fontFamily = fontNeoSansArabicRegular600
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = 10.toDP)
                            .weight(1f)
                            .clip(RoundedCornerShape(8.toDP))
                            .border(
                                width = 1.toDP,
                                color = if (!viewModel.isAyaHighlight) colorPrimary else gray_200,
                                shape = RoundedCornerShape(8.toDP)
                            )
                            .clickable(
                                onClick = {
                                    viewModel.isAyaHighlight = false
                                }
                            )
                            .background(if (!viewModel.isAyaHighlight) colorPrimary else White)
                            .padding(4.toDP),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.highlight_word),
                            fontSize = 14.toSP,
                            color = if (!viewModel.isAyaHighlight) White else colorPrimary,
                            fontFamily = fontNeoSansArabicRegular600
                        )
                    }
                }

            }

            //juz and sura name clickable
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.toDP),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            )
            {
                Text(
                    text = stringResource(R.string.click_on_juz_and_sura_names),
                    fontSize = 14.toSP,
                    color = colorPrimary,
                    fontFamily = fontNeoSansArabicRegular600
                )

                Row(
                    modifier = Modifier.padding(top = 5.toDP),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(0.toDP)
                            .weight(1f)
                            .clip(RoundedCornerShape(8.toDP))
                            .border(
                                width = 1.toDP,
                                color = if (viewModel.isEnableJuzClick) colorPrimary else gray_200,
                                shape = RoundedCornerShape(8.toDP)
                            )
                            .clickable(
                                onClick = {
                                    viewModel.isEnableJuzClick = !viewModel.isEnableJuzClick
                                }
                            )
                            .background(if (viewModel.isEnableJuzClick) colorPrimary else White)
                            .padding(4.toDP),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.juz_clickable),
                            fontSize = 14.toSP,
                            color = if (viewModel.isEnableJuzClick) White else colorPrimary,
                            fontFamily = fontNeoSansArabicRegular600
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = 10.toDP)
                            .weight(1f)
                            .clip(RoundedCornerShape(8.toDP))
                            .border(
                                width = 1.toDP,
                                color = if (viewModel.isEnableSuraClick) colorPrimary else gray_200,
                                shape = RoundedCornerShape(8.toDP)
                            )
                            .clickable(
                                onClick = {
                                    viewModel.isEnableSuraClick = !viewModel.isEnableSuraClick
                                }
                            )
                            .background(if (viewModel.isEnableSuraClick) colorPrimary else White)
                            .padding(4.toDP),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.sura_clickable),
                            fontSize = 14.toSP,
                            color = if (viewModel.isEnableSuraClick) White else colorPrimary,
                            fontFamily = fontNeoSansArabicRegular600
                        )
                    }
                }

            }
            //bold font
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.toDP),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            )
            {
                Text(
                    text = stringResource(R.string.font_weight),
                    fontSize = 14.toSP,
                    color = colorPrimary,
                    fontFamily = fontNeoSansArabicRegular600
                )

                Row(
                    modifier = Modifier.padding(top = 5.toDP),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(0.toDP)
                            .weight(1f)
                            .clip(RoundedCornerShape(8.toDP))
                            .border(
                                width = 1.toDP,
                                color = if (viewModel.isBoldFont) colorPrimary else gray_200,
                                shape = RoundedCornerShape(8.toDP)
                            )
                            .clickable(
                                onClick = {
                                    viewModel.isBoldFont = true
                                }
                            )
                            .background(if (viewModel.isBoldFont) colorPrimary else White)
                            .padding(4.toDP),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.bold),
                            fontSize = 14.toSP,
                            color = if (viewModel.isBoldFont) White else colorPrimary,
                            fontFamily = fontNeoSansArabicRegular600
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = 10.toDP)
                            .weight(1f)
                            .clip(RoundedCornerShape(8.toDP))
                            .border(
                                width = 1.toDP,
                                color = if (!viewModel.isBoldFont) colorPrimary else gray_200,
                                shape = RoundedCornerShape(8.toDP)
                            )
                            .clickable(
                                onClick = {
                                    viewModel.isBoldFont = false
                                }
                            )
                            .background(if (!viewModel.isBoldFont) colorPrimary else White)
                            .padding(4.toDP),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.regular),
                            fontSize = 14.toSP,
                            color = if (!viewModel.isBoldFont) White else colorPrimary,
                            fontFamily = fontNeoSansArabicRegular600
                        )
                    }
                }
            }

            //page to open
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.toDP),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            )
            {
                Text(
                    text = stringResource(R.string.page_number),
                    fontSize = 14.toSP,
                    color = colorPrimary,
                    fontFamily = fontNeoSansArabicRegular600
                )

                CustomTextField(
                    modifier = Modifier.padding(top = 5.toDP),
                    hint = "",
                    hintColor = gray_200,
                    placeholder = "",
                    onValueChange = {
                        viewModel.pageToOpen = it.toIntOrNull() ?: 1
                    },
                    isNumber = true,
                    padding = 10,
                    roundCorner = 8,
                    errorMessage = "",
                    text = viewModel.pageToOpen.toString(),
                    textSize = 14,
                    errorTextSize = 1,
                    textColor = colorPrimary,
                    backgroundColor = White,
                    errorTextColor = colorPrimary,
                    errorBackgroundColor = White,
                    errorColorBorder = colorPrimary,
                    enabled = true,
                    hasError = false,
                    focusedIndicatorColor = transparent,
                    unFocusedIndicatorColor = transparent,
                    borderWidth = 1,
                    unFocusColorBorder = gray_200,
                    focusColorBorder = colorPrimary,
                    textFontFamily = fontNeoSansArabicRegular400
                )
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
                                ).apply {
                                    putExtra("pageToOpen", viewModel.pageToOpen)
                                    putExtra("isAyaHighlight", viewModel.isAyaHighlight)
                                    putExtra("isEnableJuzClick", viewModel.isEnableJuzClick)
                                    putExtra("isEnableSuraClick", viewModel.isEnableSuraClick)
                                    putExtra("isBoldFont", viewModel.isBoldFont)

                                    //bg color
                                    val selectedBGColor =
                                        viewModel.bgColors.firstOrNull { it.selected }
                                    putExtra(
                                        "selectedBGColor",
                                        selectedBGColor?.colorCode ?: "FDF8F2"
                                    )
                                    //font color
                                    val selectedFontColor =
                                        viewModel.fontColors.firstOrNull { it.selected }
                                    putExtra(
                                        "selectedFontColor",
                                        selectedFontColor?.colorCode ?: "000000"
                                    )
                                    //sura header
                                    val selectedSurahHeaderColor =
                                        viewModel.surahHeaderColors.firstOrNull { it.selected }
                                    putExtra(
                                        "selectedSurahHeaderColor",
                                        selectedSurahHeaderColor?.colorCode ?: "000000"
                                    )
                                    //sura title color
                                    val selectedSurahTitleColor =
                                        viewModel.surahTitleColors.firstOrNull { it.selected }
                                    putExtra(
                                        "selectedSurahTitleColor",
                                        selectedSurahTitleColor?.colorCode ?: "000000"
                                    )
                                    //aya number color
                                    val selectedAyaNumberColor =
                                        viewModel.ayaNumberColors.firstOrNull { it.selected }
                                    putExtra(
                                        "selectedAyahNumberColor",
                                        selectedAyaNumberColor?.colorCode ?: "000000"
                                    )
                                    //highlight color
                                    val selectedHighlightColor =
                                        viewModel.highlightColors.firstOrNull { it.selected }
                                    putExtra(
                                        "selectedHighlightColor",
                                        selectedHighlightColor?.colorCode ?: "DBEBF7"
                                    )
                                }
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
