package com.blacksmith.quranApp.presentation.quran

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import com.blacksmith.quranApp.R
import com.blacksmith.quranlib.data.model.AyaModel
import com.blacksmith.quranlib.data.util.component.ComposableLifecycle
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranApp.presentation.base.theme.gray_400
import com.blacksmith.quranlib.data.util.helper.toSP
import com.blacksmith.quranlib.presentation.quranScreen.QuranPageCanvasModeScreen
import com.blacksmith.quranlib.presentation.quranScreen.QuranPageTextModeScreen
import kotlinx.coroutines.delay

@Composable
fun QuranScreen(viewModel: QuranViewModel) {
    val context = LocalContext.current
    ComposableLifecycle { source, event ->
        when (event) {
            Lifecycle.Event.ON_STOP -> { viewModel.onDispose() }
            else -> {}
        }
    }
    Content(context, viewModel)
}

@Composable
fun Content(context: Context = LocalContext.current, viewModel: QuranViewModel) {
    val context = LocalContext.current
    val fontColor = Color(viewModel.fontColor.toColorInt())
    val bgColor = Color(viewModel.bgColor.toColorInt())

    BackHandler(enabled = viewModel.isSearchVisible) { viewModel.hideSearch() }

    LaunchedEffect(viewModel.searchQuery) {
        delay(300L)
        if (viewModel.searchQuery.length >= 2) {
            viewModel.searchAyas(context, viewModel.searchQuery)
        } else {
            viewModel.clearSearchResults()
        }
    }

    Surface(color = bgColor, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ─── AppBar ───────────────────────────────────────────────────────
            if (!viewModel.isSearchVisible) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.toDP, vertical = 8.toDP),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .width(30.toDP).height(30.toDP).clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = gray_400),
                                onClick = { (context as QuranActivity).finish() }
                            )
                            .padding(5.toDP),
                        tint = fontColor,
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "",
                    )
                    Text(
                        text = stringResource(R.string.quran),
                        modifier = Modifier.padding(start = 10.toDP),
                        color = fontColor,
                        fontSize = 16.toSP,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    // أيقونة البحث على اليسار (نهاية الـ Row)
                    Icon(
                        modifier = Modifier
                            .size(36.toDP).clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = gray_400),
                                onClick = { viewModel.showSearch() }
                            )
                            .padding(6.toDP),
                        tint = fontColor,
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                    )
                }
            } else {
                SearchBar(
                    query = viewModel.searchQuery,
                    onQueryChange = { viewModel.searchQuery = it },
                    onClose = { viewModel.hideSearch() },
                    fontColor = fontColor,
                )
            }

            // ─── صفحة القرآن + dropdown السيرش ───────────────────────────────
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                if (viewModel.isText)
                    QuranPageTextModeScreen(
                        isReversePager = !viewModel.isArabicLocale(),
                        fontColor = fontColor,
                        quranPagesVersion = viewModel.quranPagesVersion,
                        suraHeaderColor = Color(viewModel.surahHeaderColor.toColorInt()),
                        suraNameColor = Color(viewModel.surahTitleColor.toColorInt()),
                        highlightColor = Color(viewModel.highlightColor.toColorInt()),
                        pageBackground = bgColor,
                        ayahNumberColor = Color(viewModel.ayahNumberColor.toColorInt()),
                        isAyaHighlight = viewModel.isAyaHighlight,
                        isSurahClickable = viewModel.isEnableSuraClick,
                        isFontBold = viewModel.isBoldFont,
                        pageToOpen = viewModel.pageToOpen,
                        isJuzClickable = viewModel.isEnableJuzClick,
                        onClickJuzName = {},
                        onClickSurahName = {}
                    )
                else
                    QuranPageCanvasModeScreen(
                        isReversePager = !viewModel.isArabicLocale(),
                        fontColor = fontColor,
                        quranPagesVersion = viewModel.quranPagesVersion,
                        suraHeaderColor = Color(viewModel.surahHeaderColor.toColorInt()),
                        suraNameColor = Color(viewModel.surahTitleColor.toColorInt()),
                        highlightColor = Color(viewModel.highlightColor.toColorInt()),
                        pageBackground = bgColor,
                        ayahNumberColor = Color(viewModel.ayahNumberColor.toColorInt()),
                        isAyaHighlight = viewModel.isAyaHighlight,
                        isSurahClickable = viewModel.isEnableSuraClick,
                        isFontBold = viewModel.isBoldFont,
                        pageToOpen = viewModel.pageToOpen,
                        isJuzClickable = viewModel.isEnableJuzClick,
                        onClickJuzName = {},
                        onClickSurahName = {}
                    )

                // نتائج البحث تظهر فوق القرآن
                androidx.compose.animation.AnimatedVisibility (
                    visible = viewModel.isSearchVisible &&
                            (viewModel.searchResults.isNotEmpty() || viewModel.isSearchLoading),
                    modifier = Modifier.align(Alignment.TopStart),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    SearchResultsDropdown(
                        results = viewModel.searchResults,
                        isLoading = viewModel.isSearchLoading,
                        fontColor = fontColor,
                        bgColor = bgColor,
                        onItemClick = { aya ->
                            viewModel.pageToOpen = aya.page ?: 1
                            viewModel.hideSearch()
                        }
                    )
                }
            }
        }
    }
}

// =============================================================================
// SearchBar
// =============================================================================
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    fontColor: Color,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.toDP, vertical = 8.toDP),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(36.toDP).clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = gray_400),
                    onClick = onClose
                )
                .padding(6.toDP),
            tint = fontColor,
            imageVector = Icons.Default.Close,
            contentDescription = "Close search",
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.toDP)
                .clip(RoundedCornerShape(20.dp))
                .background(fontColor.copy(alpha = 0.08f))
                .padding(horizontal = 12.toDP, vertical = 8.toDP),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isEmpty()) {
                Text(
                    text = "ابحث في القرآن الكريم...",
                    color = fontColor.copy(alpha = 0.45f),
                    fontSize = 14.toSP,
                    style = TextStyle(textDirection = TextDirection.Rtl),
                )
            }
            BasicTextField(
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = fontColor,
                    fontSize = 14.toSP,
                    textDirection = TextDirection.Content,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                cursorBrush = SolidColor(fontColor),
            )
        }
    }
}

// =============================================================================
// SearchResultsDropdown
// =============================================================================
@Composable
private fun SearchResultsDropdown(
    results: List<AyaModel>,
    isLoading: Boolean,
    fontColor: Color,
    bgColor: Color,
    onItemClick: (AyaModel) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 320.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(
                color = bgColor.copy(alpha = 0.97f),
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            )
    ) {
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.toDP),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = fontColor, strokeWidth = 2.dp)
                }
            }
        }
        items(items = results, key = { it.id ?: it.hashCode().toString() }) { aya ->
            AyaSearchItem(aya = aya, fontColor = fontColor, onClick = { onItemClick(aya) })
            HorizontalDivider(color = fontColor.copy(alpha = 0.08f), thickness = 0.5.dp)
        }
    }
}

// =============================================================================
// AyaSearchItem
// =============================================================================
@Composable
private fun AyaSearchItem(aya: AyaModel, fontColor: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.toDP, vertical = 10.toDP),
    ) {
        // السطر الأول: نص الآية بالتشكيل
        Text(
            text = aya.text ?: "",
            color = fontColor,
            fontSize = 15.toSP,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(textDirection = TextDirection.Rtl),
        )
        // السطر الثاني: بيانات السورة والآية والصفحة
        Text(
            text = buildAyaInfo(aya),
            color = fontColor.copy(alpha = 0.6f),
            fontSize = 12.toSP,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(textDirection = TextDirection.Rtl),
            modifier = Modifier.padding(top = 3.toDP),
        )
    }
}

private fun buildAyaInfo(aya: AyaModel): String {
    val surahNumber = aya.surah?.id ?: 0
    val surahName = aya.surah?.name_ar ?: ""
    val ayaNumber = aya.aya ?: ""
    val page = aya.page ?: 0
    return "سورة ($surahNumber) $surahName • آية :$ayaNumber • (صفحة $page)"
}
