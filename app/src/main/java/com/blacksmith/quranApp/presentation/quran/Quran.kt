package com.blacksmith.quranApp.presentation.quran

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.RectF
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import com.blacksmith.quranApp.R
import com.blacksmith.quranApp.presentation.base.theme.gray_400
import com.blacksmith.quranlib.data.model.WordModel
import com.blacksmith.quranlib.data.util.component.ComposableLifecycle
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranlib.data.util.helper.toSP
import com.blacksmith.quranlib.presentation.quranScreen.QuranPageCanvasModeScreen
import com.blacksmith.quranlib.presentation.quranScreen.WordMenuPositionProvider
import com.blacksmith.quranlib.presentation.theme.Black
import com.blacksmith.quranlib.presentation.theme.White
import kotlinx.coroutines.delay
import kotlin.collections.emptyList

@Composable
fun QuranScreen(viewModel: QuranViewModel) {
    val context = LocalContext.current
    ComposableLifecycle { source, event ->
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                viewModel.onDispose()
            }

            else -> {}
        }
    }
    val fontColor = Color(viewModel.fontColor.toColorInt())
    val bgColor = Color(viewModel.bgColor.toColorInt())
    if (viewModel.isIndexVisible) {
        QuranIndexBottomSheet(
            viewModel = viewModel,
            fontColor = fontColor,
            bgColor = bgColor,
            onSurahClick = { page ->
                viewModel.pageToOpen = page
                viewModel.hideIndex()
            }
        )
    }
    Content(context, viewModel, bgColor, fontColor)
}

@Composable
fun Content(
    context: Context = LocalContext.current,
    viewModel: QuranViewModel,
    bgColor: Color,
    fontColor: Color
) {
    val context = LocalContext.current


    BackHandler(enabled = viewModel.isSearchVisible) {
        viewModel.hideSearch()
    }

    // Debounce 300ms
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
        )
        {
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
                            .width(30.toDP)
                            .height(30.toDP)
                            .clip(CircleShape)
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
                    // ── Index icon ──────────────────────────────────────────
                    Icon(
                        modifier = Modifier
                            .size(36.toDP)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = gray_400),
                                onClick = { viewModel.showIndex(context) }
                            )
                            .padding(6.toDP),
                        tint = fontColor,
                        imageVector = Icons.Default.List,
                        contentDescription = "Quran Index",
                    )
                    // ── Search icon ─────────────────────────────────────────
                    Icon(
                        modifier = Modifier
                            .size(36.toDP)
                            .clip(CircleShape)
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                var selectedWord by remember { mutableStateOf<WordModel?>(null) }
                var selectedAyahId by remember { mutableStateOf<Int?>(null) }
                var selectedSurahId by remember { mutableStateOf<Int?>(null) }
                var showContextMenu by remember { mutableStateOf(false) }
                var selectedWordRectInWindow by remember { mutableStateOf(RectF()) }
                val wordsByAyah = remember {
                    mutableStateListOf<WordModel>()
                }
                val density = LocalDensity.current
                val screenWidthPx = context.resources.displayMetrics.widthPixels
                val menuMarginPx = with(density) { 6.dp.roundToPx() }
                val selectedText =
                    remember(
                        selectedWord,
                        selectedAyahId,
                        selectedSurahId,
                        viewModel.isAyaHighlight
                    ) {
                        if (viewModel.isAyaHighlight && selectedAyahId != null) {
                            "${wordsByAyah.joinToString(" ") { it.wordText }}\nسورة ${wordsByAyah.firstOrNull()?.surahName} - آية $selectedAyahId"
                        } else {
                            selectedWord?.let { "${it.wordText}\nسورة ${it.surahName} - آية ${it.ayah}" }
                                ?: ""
                        }
                    }
                val positionProvider = remember(selectedWordRectInWindow) {
                    WordMenuPositionProvider(
                        wordRectInWindow = selectedWordRectInWindow,
                        menuMarginPx = menuMarginPx,
                        screenWidthPx = screenWidthPx,
                    )
                }

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
                    onClickSurahName = {},
                    onWordLongPressed = { isAyaHighlight, wordModel, wordRectInWindow, selectedAyah, selectedSurah, ayahWords ->
                        if (isAyaHighlight) {
                            selectedAyahId = selectedAyah
                            wordsByAyah.clear()
                            wordsByAyah.addAll(ayahWords)
                        } else
                            selectedWord = wordModel
                        selectedSurahId = wordModel.surahId
                        wordRectInWindow.let {
                            selectedWordRectInWindow = it
                        }
                        showContextMenu = true
                    },
                    onWordClicked = { wordModel ->
                        showContextMenu = false
                        Toast.makeText(
                            context,
                            "تم الضغط على كلمة: ${wordModel.wordText} سورة ${wordModel.surahName} - آية ${wordModel.ayah}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                if (showContextMenu && (selectedWord != null || selectedAyahId != null)) {
                    Popup(
                        popupPositionProvider = positionProvider,
                        onDismissRequest = {
                            showContextMenu = false
                            selectedWord = null
                            selectedAyahId = null
                        },
                    ) {
                        QuranContextMenu(
                            onCopy = {
                                val cb =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cb.setPrimaryClip(ClipData.newPlainText("quran", selectedText))
                                showContextMenu = false
                                selectedWord = null
                                selectedAyahId = null
                            },
                            onShare = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, selectedText)
                                }
                                context.startActivity(Intent.createChooser(intent, null))
                                showContextMenu = false
                                selectedWord = null
                                selectedAyahId = null
                            },
                        )
                    }
                }

                // نتائج البحث فوق صفحة القرآن
                androidx.compose.animation.AnimatedVisibility(
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
                .size(36.toDP)
                .clip(CircleShape)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
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
// QuranContextMenu
// =============================================================================
@Composable
private fun QuranContextMenu(
    onCopy: () -> Unit,
    onShare: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(White, shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
    ) {
        Text(
            text = "نسخ",
            color = Black,
            modifier = Modifier
                .clickable(onClick = onCopy)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        )
        Text(
            text = "مشاركة",
            color = Black,
            modifier = Modifier
                .clickable(onClick = onShare)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        )
    }
}
