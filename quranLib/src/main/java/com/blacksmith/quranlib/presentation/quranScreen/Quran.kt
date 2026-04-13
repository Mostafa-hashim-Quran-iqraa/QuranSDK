package com.blacksmith.quranlib.presentation.quranScreen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.blacksmith.quranlib.R
import com.blacksmith.quranlib.data.model.ChapterModel
import com.blacksmith.quranlib.data.model.LineModel
import com.blacksmith.quranlib.data.model.QuranPageModel
import com.blacksmith.quranlib.data.model.RenderLine
import com.blacksmith.quranlib.data.model.RenderWord
import com.blacksmith.quranlib.data.model.SurahModel
import com.blacksmith.quranlib.data.model.WordModel
import com.blacksmith.quranlib.data.util.component.ComposableLifecycle
import com.blacksmith.quranlib.data.util.component.ErrorView
import com.blacksmith.quranlib.data.util.component.LoaderLottie
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranlib.data.util.helper.toSP
import com.blacksmith.quranlib.presentation.theme.Black
import com.blacksmith.quranlib.presentation.theme.GreenDark
import com.blacksmith.quranlib.presentation.theme.White
import com.blacksmith.quranlib.presentation.theme.amiri_quran
import com.blacksmith.quranlib.presentation.theme.colorPrimary
import com.blacksmith.quranlib.presentation.theme.colorPrimaryMoreLight
import com.blacksmith.quranlib.presentation.theme.surah_name_v2
import kotlinx.coroutines.CoroutineScope
import kotlin.collections.plus
import kotlin.math.abs

@Composable
fun QuranPageScreen(
    viewModel: QuranViewModel = hiltViewModel(),
    isReversePager: Boolean = false,
    pageBackground: Color = White,
    fontColor: Color = Black,
    suraHeaderColor: Color = GreenDark,
    suraNameColor: Color = GreenDark,
    highlightColor: Color = colorPrimaryMoreLight,
    isAyaHighlight: Boolean = false,
    isSurahClickable: Boolean = false,
    isJuzClickable: Boolean = false,
    isFontBold: Boolean = false,
    pageToOpen: Int = 0,
    onClickJuzName: (ChapterModel) -> Unit = { chapterModel -> },
    onClickSurahName: (SurahModel) -> Unit = { surahModel -> }
) {
    val context = LocalContext.current
    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
            }

            Lifecycle.Event.ON_START -> {
                if (!viewModel.isDataLoaded) viewModel.getData(context)
            }

            Lifecycle.Event.ON_RESUME -> {
            }

            Lifecycle.Event.ON_PAUSE -> {
            }

            Lifecycle.Event.ON_STOP -> {
            }

            Lifecycle.Event.ON_DESTROY -> {
            }

            else -> {}
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage =
            if (pageToOpen <= 0) 0 else
                if (pageToOpen >= 604) 603 else (pageToOpen - 1), pageCount = { 604 })

    Content(
        context = context,
        pagerState = pagerState,
        coroutineScope = coroutineScope,
        viewModel = viewModel,
        isReversePager = isReversePager,
        pageBackground = pageBackground,
        fontColor = fontColor,
        suraHeaderColor = suraHeaderColor,
        suraNameColor = suraNameColor,
        highlightColor = highlightColor,
        isAyaHighlight = isAyaHighlight,
        isSurahClickable = isSurahClickable,
        isJuzClickable = isJuzClickable,
        onClickJuzName = onClickJuzName,
        onClickSurahName = onClickSurahName,
        isFontBold = isFontBold,

        )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Content(
    context: Context = LocalContext.current,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    viewModel: QuranViewModel,
    isReversePager: Boolean,
    pageBackground: Color,
    fontColor: Color,
    suraHeaderColor: Color,
    suraNameColor: Color,
    highlightColor: Color,
    isAyaHighlight: Boolean,
    isSurahClickable: Boolean,
    isJuzClickable: Boolean,
    isFontBold: Boolean,
    onClickJuzName: (ChapterModel) -> Unit = { chapterModel -> },
    onClickSurahName: (SurahModel) -> Unit = { surahModel -> }
) {
    Surface(
        color = pageBackground, modifier = Modifier.fillMaxSize()
    ) {
        //content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackground),
            contentAlignment = Alignment.Center
        ) {

            //Loading
            if (viewModel.isShowLoader) {
                LoaderLottie(R.raw.loader_circle, colorPrimary, 40, 40)
                return@Box
            }
            //error
            if (viewModel.isShowError) {
                ErrorView(
                    title = "Error",
                    message = "Error",
                    onClick = {
                        viewModel.getData(context)
                    },
                )
                return@Box
            }
            //content
            if (!viewModel.isShowLoader && !viewModel.isShowError) {
                Box(
                    modifier = Modifier
                        .padding(top = 0.toDP)
                        .fillMaxSize()
                        .padding(bottom = 10.dp)
                ) {
                    //screen content
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        //pager
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            //make direction always from right to left, so reverse layout in english locale
                            reverseLayout = isReversePager,
                            beyondViewportPageCount = 2
                        ) { currentPage ->
                            val shouldRender = abs(pagerState.currentPage - currentPage) <= 1
                            if (shouldRender) {
                                val pageModel = remember(currentPage) {
                                    viewModel.quranPageModels[currentPage]
                                }
                                val pageNumber =
                                    remember(currentPage) { toArabicNumber(currentPage + 1) }

                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                )
                                {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val chapterName = remember(pageModel) {
                                            pageModel.chapterModel.name_ar ?: ""
                                        }
                                        Text(
                                            text = chapterName,
                                            color = fontColor,
                                            fontSize = 14.toSP,
                                            fontFamily = amiri_quran,
                                            textAlign = TextAlign.Center,
                                            textDecoration = if (isJuzClickable) TextDecoration.Underline
                                            else TextDecoration.None,
                                            modifier = Modifier.clickable {
                                                if (isJuzClickable)
                                                    onClickJuzName.invoke(pageModel.chapterModel)
                                            }
                                        )

                                        val surahName = remember(pageModel) {
                                            pageModel.surahModel.name_ar ?: ""
                                        }
                                        Text(
                                            text = surahName,
                                            color = fontColor,
                                            fontSize = 14.toSP,
                                            fontFamily = amiri_quran,
                                            textAlign = TextAlign.Center,
                                            textDecoration = if (isSurahClickable) TextDecoration.Underline
                                            else TextDecoration.None,
                                            modifier = Modifier.clickable {
                                                if (isSurahClickable)
                                                    onClickSurahName.invoke(pageModel.surahModel)
                                            })
                                    }
                                    val quranFont = viewModel.getFontForPage(context, currentPage + 1)
                                    if (quranFont != null) {
                                        Page(
                                            context = context,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f),
                                            viewModel = viewModel,
                                            pageModel = pageModel,
                                            currentPage = currentPage,
                                            fontColor = fontColor,
                                            quranFont = quranFont,
                                            suraHeaderColor = suraHeaderColor,
                                            suraNameColor = suraNameColor,
                                            highlightColor = highlightColor,
                                            isAyaHighlight = isAyaHighlight,
                                            isFontBold = isFontBold,
                                        )
                                    }else{
                                        Box(modifier = Modifier.fillMaxWidth()
                                            .weight(1f))
                                    }
                                    Text(
                                        color = fontColor,
                                        text = pageNumber,
                                        fontFamily = amiri_quran,
                                        fontSize = 14.toSP,
                                    )
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Page(
    context: Context, viewModel: QuranViewModel, currentPage: Int,
    modifier: Modifier,
    pageModel: QuranPageModel,
    fontColor: Color,
    quranFont: FontFamily,
    suraHeaderColor: Color,
    suraNameColor: Color,
    highlightColor: Color,
    isAyaHighlight: Boolean,
    isFontBold: Boolean,
) {
//    val clipboard = LocalClipboard.current
    val screenWidth = remember { Resources.getSystem().displayMetrics.widthPixels }

    val lines = pageModel.lines
    val wordsByAyah = remember(pageModel) {
        pageModel.lines.flatMap { it.words }.groupBy { it.surahId to it.ayah }
    }
    var selectedWord by remember { mutableStateOf<WordModel?>(null) }
    var selectedAyah by remember { mutableStateOf<Int?>(null) }
    var selectedSurah by remember { mutableStateOf<Int?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var menuOffset by remember { mutableStateOf(Offset.Zero) }

    val selectedText = remember(selectedWord, selectedAyah, selectedSurah, isAyaHighlight) {
        if (isAyaHighlight && selectedAyah != null) {
            val ayahWords = wordsByAyah[selectedSurah to selectedAyah] ?: emptyList()
            "${ayahWords.joinToString(" ") { it.wordText }}\nسورة ${ayahWords.firstOrNull()?.surahName} - آية $selectedAyah"
        } else {
            selectedWord?.let { "${it.wordText}\nسورة ${it.surahName} - آية ${it.ayah}" } ?: ""
        }
    }

    val horizontalAlignment = remember(currentPage) {
        if (currentPage > 1) Alignment.Start else Alignment.CenterHorizontally
    }
    val boldTextStyle = remember(isFontBold, fontColor) {
        if (isFontBold) TextStyle(
            shadow = Shadow(color = fontColor, offset = Offset(0.9f, 0.9f), blurRadius = 0f)
        ) else TextStyle.Default
    }

    val fontSize = remember { 20.sp }

    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 10.toDP, vertical = 0.toDP)
                .fillMaxSize(),
            userScrollEnabled = false,
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = Arrangement.spacedBy(
                5.toDP, Alignment.CenterVertically
            )
        ) {
            items(items = lines, key = { line -> line.lineNumber }) { line ->
                when (line.line_type) {
                    LineModel.LINE_TYPE_SURAH_NAME -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),

                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.surah_title),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(suraHeaderColor)
                            )
                            Text(
                                color = suraNameColor,
                                text = line.surahLigature,
                                fontFamily = surah_name_v2,
                                fontSize = 30.toSP,
                            )
                        }
                    }

                    LineModel.LINE_TYPE_BASMALAH -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),

                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier.padding(horizontal = 95.toDP),
                                painter = painterResource(R.drawable.basmala),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(fontColor)
                            )
                        }
                    }

                    else -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (line.isCentered) Arrangement.Center else Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            line.words.forEach { word ->
                                key(word.id) {
                                    WordText(
                                        word = word,
                                        isSelected = if (isAyaHighlight) {
                                            selectedAyah == word.ayah && selectedSurah == word.surahId
                                        } else selectedWord?.location == word.location,
                                        fontFamily = quranFont,
                                        fontColor = fontColor,
                                        highlightColor = highlightColor,
                                        textStyle = boldTextStyle,
                                        screenWidth = screenWidth,
                                        fontSize = fontSize,
                                        onMenuOffsetChange = { menuOffset = it },
                                        onClick = {
                                            if (showMenu) return@WordText
                                            selectedWord = null
                                            selectedAyah = null
                                            selectedSurah = null
                                            showMenu = false
                                        },
                                        onLongClick = {
                                            if (isAyaHighlight) {
                                                selectedAyah = word.ayah
                                                selectedWord = null
                                            } else {
                                                selectedWord = word
                                                selectedAyah = null
                                            }
                                            selectedSurah = word.surahId
                                            showMenu = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = showMenu && (selectedWord != null || selectedAyah != null)) {
            Popup(
//                alignment = Alignment.TopEnd,
                offset = IntOffset(
                    menuOffset.x.toInt(), menuOffset.y.toInt()
                ), onDismissRequest = {
                    showMenu = false
                    selectedWord = null
                }) {
                Column(
                    modifier = Modifier
                        .background(White, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                ) {

                    // Copy
                    Text(
                        text = "Copy", color = Black, modifier = Modifier
                            .clickable {
                                val clipboard =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                                val clip = ClipData.newPlainText("text", selectedText)
                                clipboard.setPrimaryClip(clip)
                                showMenu = false
                                selectedWord = null
                                selectedAyah = null
                            }
                            .padding(12.dp))

                    // Share
                    Text(
                        text = "Share", color = Black, modifier = Modifier
                            .clickable {
//                                shareToOtherApps(context, selectedText)
                                showMenu = false
                                selectedWord = null
                                selectedAyah = null
                            }
                            .padding(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WordText(
    word: WordModel,
    isSelected: Boolean,
    fontFamily: FontFamily,
    fontColor: Color,
    highlightColor: Color,
    textStyle: TextStyle,
    screenWidth: Int,
    fontSize: TextUnit,
    onMenuOffsetChange: (Offset) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    // ✅ onGloballyPositioned runs ONLY for the selected word
    val positionModifier = if (isSelected) {
        Modifier.onGloballyPositioned { coordinates ->
            val bounds = coordinates.boundsInRoot()
            val position = coordinates.positionInWindow()
            val size = coordinates.size
            val menuWidth = 180f
            val menuHeight = 120f
            val mirroredX = screenWidth - position.x - size.width
            val x = if (mirroredX + size.width + menuWidth > screenWidth)
                mirroredX - menuWidth
            else
                mirroredX + size.width
            val y = bounds.top - (size.height * 0.8f) - menuHeight - 12f
            onMenuOffsetChange(Offset(x, y))
        }
    } else Modifier

    Text(
        text = word.text,
        color = fontColor,
        style = textStyle,
        fontFamily = fontFamily,
        fontSize = fontSize,
        modifier = Modifier
            .background(if (isSelected) highlightColor else Color.Transparent)
            .then(positionModifier)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    )
}

fun toArabicNumber(number: Int): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return number.toString().map { arabicDigits[it - '0'] }.joinToString("")
}