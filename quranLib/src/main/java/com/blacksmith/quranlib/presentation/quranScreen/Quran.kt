package com.blacksmith.quranlib.presentation.quranScreen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
import com.blacksmith.quranlib.data.model.SurahModel
import com.blacksmith.quranlib.data.model.WordModel
import com.blacksmith.quranlib.data.util.component.ComposableLifecycle
import com.blacksmith.quranlib.data.util.component.ErrorView
import com.blacksmith.quranlib.data.util.component.LoaderLottie
import com.blacksmith.quranlib.data.util.helper.dpToPx
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranlib.data.util.helper.toSP
import com.blacksmith.quranlib.presentation.theme.Black
import com.blacksmith.quranlib.presentation.theme.GreenDark
import com.blacksmith.quranlib.presentation.theme.White
import com.blacksmith.quranlib.presentation.theme.amiri_quran
import com.blacksmith.quranlib.presentation.theme.colorPrimary
import com.blacksmith.quranlib.presentation.theme.colorPrimaryMoreLight
import com.blacksmith.quranlib.presentation.theme.red_light
import kotlinx.coroutines.CoroutineScope

@Composable
fun QuranPageScreen(
    viewModel: QuranViewModel = hiltViewModel(),
    isReversePager: Boolean = false,
    pageBackground: Color = White,
    fontColor: Color = Black,
    suraHeaderColor: Color = GreenDark,
    suraNameColor: Color = GreenDark,
    ayahNumberColor: Color = red_light,
    highlightColor: Color = colorPrimaryMoreLight,
    isAyaHighlight: Boolean = false,
    isSurahClickable: Boolean = false,
    isJuzClickable: Boolean = false,
    isFontBold: Boolean = false,
    pageToOpen: Int = 0,
    onClickJuzName: (ChapterModel) -> Unit = {},
    onClickSurahName: (SurahModel) -> Unit = {}
) {
    val context = LocalContext.current
    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                if (!viewModel.isDataLoaded) viewModel.getData(context)
            }

            else -> {}
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage =
            if (pageToOpen <= 0) 0 else
                if (pageToOpen >= 604) 603 else (pageToOpen - 1),
        pageCount = { 604 }
    )

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
        ayahNumberColor = ayahNumberColor,
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
    ayahNumberColor: Color,
    suraHeaderColor: Color,
    suraNameColor: Color,
    highlightColor: Color,
    isAyaHighlight: Boolean,
    isSurahClickable: Boolean,
    isJuzClickable: Boolean,
    isFontBold: Boolean,
    onClickJuzName: (ChapterModel) -> Unit = {},
    onClickSurahName: (SurahModel) -> Unit = {}
) {
    LaunchedEffect(pagerState.currentPage) {
        viewModel.preloadFontsAround(context, pagerState.currentPage + 1, range = 3)
    }

    Surface(color = pageBackground, modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackground),
            contentAlignment = Alignment.Center
        ) {
            if (viewModel.isShowLoader) {
                LoaderLottie(R.raw.loader_circle, colorPrimary, 40, 40)
                return@Box
            }
            if (viewModel.isShowError) {
                ErrorView(
                    title = "Error",
                    message = "Error",
                    onClick = { viewModel.getData(context) },
                )
                return@Box
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        reverseLayout = isReversePager,
                        beyondViewportPageCount = 1
                    ) { currentPage ->
                        if (currentPage < viewModel.quranPageModels.size) {
                            val pageModel = remember(currentPage) {
                                viewModel.quranPageModels[currentPage]
                            }
                            val pageNumber = remember(currentPage) {
                                toArabicNumber(currentPage + 1)
                            }

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
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
                                        textDecoration = if (isJuzClickable) TextDecoration.Underline else TextDecoration.None,
                                        modifier = Modifier.clickable {
                                            if (isJuzClickable) onClickJuzName.invoke(pageModel.chapterModel)
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
                                        textDecoration = if (isSurahClickable) TextDecoration.Underline else TextDecoration.None,
                                        modifier = Modifier.clickable {
                                            if (isSurahClickable) onClickSurahName.invoke(pageModel.surahModel)
                                        }
                                    )
                                }

                                val fontReady = viewModel.fontReadyState[currentPage + 1]
                                val typeface = remember(currentPage, fontReady) {
                                    viewModel.getTypefaceForPage(context, currentPage + 1)
                                }

                                if (typeface != null) {
                                    val quranFont = remember(typeface) { FontFamily(typeface) }
                                    Page(
                                        context = context,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        viewModel = viewModel,
                                        pageModel = pageModel,
                                        currentPage = currentPage,
                                        fontColor = fontColor,
                                        ayahNumberColor = ayahNumberColor,
                                        quranFont = quranFont,
                                        suraHeaderColor = suraHeaderColor,
                                        suraNameColor = suraNameColor,
                                        highlightColor = highlightColor,
                                        isAyaHighlight = isAyaHighlight,
                                        isFontBold = isFontBold,
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = suraHeaderColor,
                                            modifier = Modifier.size(32.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
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


@Composable
fun Page(
    context: Context,
    viewModel: QuranViewModel,
    currentPage: Int,
    modifier: Modifier,
    pageModel: QuranPageModel,
    fontColor: Color,
    quranFont: FontFamily,
    ayahNumberColor: Color,
    suraHeaderColor: Color,
    suraNameColor: Color,
    highlightColor: Color,
    isAyaHighlight: Boolean,
    isFontBold: Boolean,
) {
    val density = LocalDensity.current
    val screenWidth = remember { Resources.getSystem().displayMetrics.widthPixels }

    val lines = pageModel.lines
    val wordsByAyah = remember(pageModel) {
        pageModel.lines.flatMap { it.words }.groupBy { it.surahId to it.ayah }
    }

    var selectedWord by remember { mutableStateOf<WordModel?>(null) }
    var selectedAyah by remember { mutableStateOf<Int?>(null) }
    var selectedSurah by remember { mutableStateOf<Int?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var menuOffset by remember { mutableStateOf(IntOffset.Zero) }

    val selectedText = remember(selectedWord, selectedAyah, selectedSurah, isAyaHighlight) {
        if (isAyaHighlight && selectedAyah != null) {
            val ayahWords = wordsByAyah[selectedSurah to selectedAyah] ?: emptyList()
            "${ayahWords.joinToString(" ") { it.wordText }}\nسورة ${ayahWords.firstOrNull()?.surahName} - آية $selectedAyah"
        } else {
            selectedWord?.let { "${it.wordText}\nسورة ${it.surahName} - آية ${it.ayah}" } ?: ""
        }
    }

    val boldTextStyle = remember(isFontBold, fontColor) {
        if (isFontBold) TextStyle(
            shadow = Shadow(color = fontColor, offset = Offset(0.9f, 0.9f), blurRadius = 0f)
        ) else TextStyle.Default
    }

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val fontSize = remember(screenWidthDp) { (screenWidthDp * 0.055f).sp }

    // wordBounds: wordId → Rect بإحداثيات الـ Window بالـ px
    val wordBounds = remember { mutableStateMapOf<Int, Rect>() }

    // offset الـ Box الأب (Page) في الـ Window — الـ Popup بيحسب offset نسبي منه
    var pageBoxOffsetInWindow by remember { mutableStateOf(Offset.Zero) }

    val selectedIds: Set<Int> =
        remember(selectedWord, selectedAyah, selectedSurah, isAyaHighlight) {
            when {
                isAyaHighlight && selectedAyah != null -> {
                    pageModel.lines
                        .flatMap { it.words }
                        .filter { it.ayah == selectedAyah && it.surahId == selectedSurah }
                        .map { it.id }
                        .toSet()
                }

                !isAyaHighlight && selectedWord != null -> {
                    setOf(selectedWord!!.id)
                }

                else -> emptySet()
            }
        }

    fun clearSelection() {
        selectedWord = null
        selectedAyah = null
        selectedSurah = null
        showMenu = false
        wordBounds.clear()
    }

    // ── حساب menuOffset ────────────────────────────────────────────────────────
    // boundsInWindow بتديك إحداثيات بالـ px في الـ Window
    // الـ Popup.offset بياخد IntOffset نسبي من مكان الـ Popup anchor (= الـ Box الأب)
    // فالـ offset الصح = (موقع التحديد في الـ Window) - (موقع الـ Box الأب في الـ Window)
    LaunchedEffect(selectedIds, wordBounds.size, pageBoxOffsetInWindow) {
        if (selectedIds.isEmpty()) return@LaunchedEffect
        val bounds = selectedIds.mapNotNull { wordBounds[it] }
        if (bounds.isEmpty()) return@LaunchedEffect

        val selLeft = bounds.minOf { it.left }
        val selRight = bounds.maxOf { it.right }
        val selBottom = bounds.maxOf { it.bottom }

        val selTop = bounds.minOf { it.top }
        val relTop = selTop - pageBoxOffsetInWindow.y


        // تحويل من Window إلى نسبي للـ Box الأب
        val relLeft = selLeft - pageBoxOffsetInWindow.x
        val relRight = selRight - pageBoxOffsetInWindow.x
        val relBottom = selBottom - pageBoxOffsetInWindow.y

        val menuWidthPx = with(density) { 180.dp.toPx() }
        val menuX = (relLeft + (relRight - relLeft) / 2f - menuWidthPx / 2f)
            .coerceIn(0f, screenWidth - menuWidthPx - pageBoxOffsetInWindow.x)

        //if from top
//        val menuY = relTop - with(density) { 6.dp.toPx() }

        val menuHeightPx = with(density) { 96.dp.toPx() }
        val menuY = if (relTop - menuHeightPx - with(density) { 6.dp.toPx() } >= 0f) {
            // فوق التحديد — نهاية المنيو فوق أعلى التحديد
            relTop - with(density) { 6.dp.toPx() }
        } else {
            // مفيش مكان فوق → تحت التحديد
            val selBottom = bounds.maxOf { it.bottom }
            val relBottom = selBottom - pageBoxOffsetInWindow.y
            relBottom + with(density) { 6.dp.toPx() }
        }

        menuOffset = IntOffset(menuX.toInt(), menuY.toInt())
    }

    Box(
        modifier = modifier.onGloballyPositioned { coords ->
            // نسجل مكان الـ Box الأب في الـ Window مرة واحدة
            pageBoxOffsetInWindow = coords.positionInWindow()
        }
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val lineHeightDp = remember(constraints.maxHeight) {
                with(density) { (constraints.maxHeight / QuranViewModel.MUSHAF_LINES_PER_PAGE.toFloat()).toDp() }
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(
                    if (pageModel.pageNumber > 2) 0.dp else 5.dp,
                    Alignment.CenterVertically
                )
//                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
            ) {
                lines.forEach { line ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(lineHeightDp),
                        contentAlignment = Alignment.Center
                    ) {
                        key(line.lineNumber) {
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
                                            fontFamily = FontFamily(viewModel.typefaceSuraName!!),
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
                                    val words = line.words
                                    val lineHighlightedIds = remember(selectedIds) {
                                        words.map { it.id }.filter { it in selectedIds }.toSet()
                                    }

                                    Box(modifier = Modifier.fillMaxWidth()) {

                                        if (line.isCentered) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                words.forEach { word ->
                                                    key(word.id) {
                                                        val isWordSelected =
                                                            word.id in lineHighlightedIds
                                                        val isAyahNum =
                                                            word.wordText.isNotEmpty() &&
                                                                    word.wordText.all { it in '٠'..'٩' }

                                                        WordText(
                                                            modifier = Modifier.onGloballyPositioned { coords ->
                                                                if (isWordSelected) {
                                                                    wordBounds[word.id] =
                                                                        coords.boundsInWindow()
                                                                }
                                                            },
                                                            word = word,
                                                            fontFamily = quranFont,
                                                            fontColor = if (isAyahNum) ayahNumberColor else fontColor,
                                                            textStyle = boldTextStyle,
                                                            fontSize = fontSize,
                                                            onClick = {
                                                                if (showMenu) return@WordText
                                                                clearSelection()
                                                            },
                                                            onLongClick = {
                                                                clearSelection()
                                                                if (isAyaHighlight) selectedAyah =
                                                                    word.ayah
                                                                else selectedWord = word
                                                                selectedSurah = word.surahId
                                                                showMenu = true
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            Layout(
                                                modifier = Modifier.fillMaxWidth(),
                                                content = {
                                                    words.forEach { word ->
                                                        key(word.id) {
                                                            val isWordSelected =
                                                                word.id in lineHighlightedIds
                                                            val isAyahNum =
                                                                word.wordText.isNotEmpty() &&
                                                                        word.wordText.all { it in '٠'..'٩' }

                                                            WordText(
                                                                modifier = Modifier.onGloballyPositioned { coords ->
                                                                    if (isWordSelected) {
                                                                        wordBounds[word.id] =
                                                                            coords.boundsInWindow()
                                                                    }
                                                                },
                                                                word = word,
                                                                fontFamily = quranFont,
                                                                fontColor = if (isAyahNum) ayahNumberColor else fontColor,
                                                                textStyle = boldTextStyle,
                                                                fontSize = fontSize,
                                                                onClick = {
                                                                    if (showMenu) return@WordText
                                                                    clearSelection()
                                                                },
                                                                onLongClick = {
                                                                    clearSelection()
                                                                    if (isAyaHighlight) selectedAyah =
                                                                        word.ayah
                                                                    else selectedWord = word
                                                                    selectedSurah = word.surahId
                                                                    showMenu = true
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            ) { measurables, constraints ->
                                                val placeables = measurables.map {
                                                    it.measure(constraints.copy(minWidth = 0))
                                                }
                                                val totalWordsWidth = placeables.sumOf { it.width }
                                                val gaps = (placeables.size - 1).coerceAtLeast(1)
                                                val spacing =
                                                    ((constraints.maxWidth - totalWordsWidth) / gaps)
                                                        .coerceAtLeast(0)
                                                val rowHeight = placeables.maxOf { it.height }

                                                layout(constraints.maxWidth, rowHeight) {
                                                    var x = 0
                                                    placeables.forEach { placeable ->
                                                        placeable.placeRelative(
                                                            x,
                                                            (rowHeight - placeable.height) / 2
                                                        )
                                                        x += placeable.width + spacing
                                                    }
                                                }
                                            }
                                        }

                                        // ── Canvas: highlight متصل على الكلمات المختارة فقط ──
                                        if (lineHighlightedIds.isNotEmpty()) {
                                            val boundsForLine =
                                                lineHighlightedIds.mapNotNull { wordBounds[it] }

                                            if (boundsForLine.isNotEmpty()) {
                                                var boxOffsetInWindow by remember {
                                                    mutableStateOf(
                                                        Offset.Zero
                                                    )
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .matchParentSize()
                                                        .onGloballyPositioned { coords ->
                                                            boxOffsetInWindow =
                                                                coords.positionInWindow()
                                                        }
                                                ) {
                                                    Canvas(modifier = Modifier.matchParentSize()) {
                                                        val localLeft =
                                                            boundsForLine.minOf { it.left } - boxOffsetInWindow.x
                                                        val localRight =
                                                            boundsForLine.maxOf { it.right } - boxOffsetInWindow.x
                                                        val localTop =
                                                            boundsForLine.minOf { it.top } - boxOffsetInWindow.y
                                                        val localBottom =
                                                            boundsForLine.maxOf { it.bottom } - boxOffsetInWindow.y

                                                        drawRect(
                                                            color = highlightColor,
                                                            topLeft = Offset(localLeft, localTop),
                                                            size = Size(
                                                                localRight - localLeft,
                                                                localBottom - localTop
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Popup Menu ────────────────────────────────────────────────────────
        // offset نسبي من الـ Box الأب (Page) مش من الـ Window
        AnimatedVisibility(visible = showMenu && (selectedWord != null || selectedAyah != null)) {
            Popup(
                offset = menuOffset,
                onDismissRequest = { clearSelection() }
            ) {
                Column(
                    modifier = Modifier
                        .background(White, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "Copy",
                        color = Black,
                        modifier = Modifier
                            .clickable {
                                val clipboard =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("text", selectedText)
                                clipboard.setPrimaryClip(clip)
                                clearSelection()
                            }
                            .padding(12.dp)
                    )
                    Text(
                        text = "Share",
                        color = Black,
                        modifier = Modifier
                            .clickable {
                                clearSelection()
                            }
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WordText(
    modifier: Modifier,
    word: WordModel,
    fontFamily: FontFamily,
    fontColor: Color,
    textStyle: TextStyle,
    fontSize: TextUnit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Text(
        text = word.text,
        color = fontColor,
        style = textStyle,
        fontFamily = fontFamily,
        fontSize = fontSize,
        modifier = modifier
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