package com.blacksmith.quranlib.presentation.quranScreen

import android.content.Context
import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
import com.blacksmith.quranlib.data.util.helper.toSP
import com.blacksmith.quranlib.presentation.theme.Black
import com.blacksmith.quranlib.presentation.theme.GreenDark
import com.blacksmith.quranlib.presentation.theme.White
import com.blacksmith.quranlib.presentation.theme.amiri_quran
import com.blacksmith.quranlib.presentation.theme.colorPrimary
import com.blacksmith.quranlib.presentation.theme.colorPrimaryMoreLight
import kotlin.math.abs

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider
import com.blacksmith.quranlib.data.util.QuranConstants
import com.blacksmith.quranlib.data.util.helper.measureWordWidth
import com.blacksmith.quranlib.data.util.helper.toArabicNumber
import com.blacksmith.quranlib.presentation.theme.red_light

// ─── Constants ────────────────────────────────────────────────────────────────
private const val WORD_GAP_PX = 1f
private const val MUSHAF_LINES_PER_PAGE = 16

// =============================================================================
// QuranPageScreen — public entry point
// =============================================================================
@Composable
fun QuranPageCanvasModeScreen(
    viewModel: QuranViewModel = hiltViewModel(),
    quranPagesVersion: Int = QuranConstants.VERSION_KING_FAHD_1441,
    isReversePager: Boolean = false,
    pageBackground: Color = White,
    fontColor: Color = Black,
    suraHeaderColor: Color = GreenDark,
    suraNameColor: Color = GreenDark,
    highlightColor: Color = colorPrimaryMoreLight,
    ayahNumberColor: Color = red_light,
    highlightType: Int = QuranConstants.HIGHLIGHT_TYPE_AYA,
    isSurahClickable: Boolean = false,
    isJuzClickable: Boolean = false,
    isFontBold: Boolean = false,
    pageToOpen: Int = 0,
    ayaNumberInSuraToHighlight: Int = -1,
    surahIdToHighlight: Int = -1,
    bookmarkedAyas: List<Pair<Int, Int>> = emptyList(),
    bookmarkHighlightColor: Color = Color(0x550073C9),
    errorWordLocations: Set<String> = emptySet(),
    errorHighlightColor: Color = Color(0xFFE53935),
    onClickJuzName: (ChapterModel) -> Unit = {},
    onClickSurahName: (SurahModel) -> Unit = {},
    // Last Int = page number (currentPage + 1) where the long-press happened
    onWordLongPressed: (highlightType: Int, WordModel, RectF, Int?, List<WordModel>, Int) -> Unit = { _, _, _, _, _, _ -> },
    // Last Int = page number (1-based) where the click happened
    onWordClicked: (WordModel, Int) -> Unit = { _, _ -> },
    onPageTap: () -> Unit = {},
) {
    val context = LocalContext.current
    ComposableLifecycle { _, event ->
        if (event == Lifecycle.Event.ON_START && !viewModel.isDataLoaded) {
            viewModel.getData(context, quranPagesVersion)
        }
    }

    val initialPage = pageToOpen.coerceIn(1, 604) - 1
    val pagerState = rememberPagerState(initialPage = initialPage) { 604 }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.preloadFontsAround(context, pagerState.currentPage + 1, range = 3)
    }

    LaunchedEffect(pageToOpen) {
        if (pageToOpen > 0) {
            val targetIndex = pageToOpen.coerceIn(1, 604) - 1
            // Start loading the target page's font proactively so it is ready
            // (or loading) by the time the pager animation finishes.
            viewModel.preloadFontsAround(context, pageToOpen, range = 1)
            if (targetIndex != pagerState.currentPage) {
                pagerState.animateScrollToPage(targetIndex)
            }
        }
    }
    QuranContent(
        context = context,
        pagerState = pagerState,
        viewModel = viewModel,
        quranPagesVersion = quranPagesVersion,
        isReversePager = isReversePager,
        pageBackground = pageBackground,
        fontColor = fontColor,
        suraHeaderColor = suraHeaderColor,
        suraNameColor = suraNameColor,
        highlightColor = highlightColor,
        ayaNumberInSuraToHighlight = ayaNumberInSuraToHighlight,
        surahIdToHighlight = surahIdToHighlight,
        bookmarkedAyas = bookmarkedAyas,
        bookmarkHighlightColor = bookmarkHighlightColor,
        errorWordLocations = errorWordLocations,
        errorHighlightColor = errorHighlightColor,
        ayahNumberColor = ayahNumberColor,
        highlightType = highlightType,
        isSurahClickable = isSurahClickable,
        isJuzClickable = isJuzClickable,
        isFontBold = isFontBold,
        onClickJuzName = onClickJuzName,
        onClickSurahName = onClickSurahName,
        onWordLongPressed = onWordLongPressed,
        onWordClicked = onWordClicked,
        onPageTap = onPageTap,
    )
}

// =============================================================================
// QuranContent — pager shell
// =============================================================================
@Composable
private fun QuranContent(
    context: Context,
    pagerState: PagerState,
    viewModel: QuranViewModel,
    quranPagesVersion: Int,
    isReversePager: Boolean,
    pageBackground: Color,
    fontColor: Color,
    suraHeaderColor: Color,
    suraNameColor: Color,
    highlightColor: Color,
    ayaNumberInSuraToHighlight: Int,
    surahIdToHighlight: Int,
    bookmarkedAyas: List<Pair<Int, Int>>,
    bookmarkHighlightColor: Color,
    errorWordLocations: Set<String>,
    errorHighlightColor: Color,
    ayahNumberColor: Color,
    highlightType: Int,
    isSurahClickable: Boolean,
    isJuzClickable: Boolean,
    isFontBold: Boolean,
    onClickJuzName: (ChapterModel) -> Unit,
    onClickSurahName: (SurahModel) -> Unit,
    onWordLongPressed: (Int, WordModel, RectF, Int?, List<WordModel>, Int) -> Unit,
    onWordClicked: (WordModel, Int) -> Unit,
    onPageTap: () -> Unit,
) {
    Surface(color = pageBackground, modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackground),
            contentAlignment = Alignment.Center,
        ) {
            when {
                viewModel.isShowLoader -> {
                    LoaderLottie(R.raw.loader_circle, colorPrimary, 40, 40)
                }

                viewModel.isShowError -> {
                    ErrorView(
                        title = "Error",
                        message = "Error",
                        onClick = { viewModel.getData(context, quranPagesVersion) },
                    )
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 10.dp),
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            reverseLayout = isReversePager,
                            beyondViewportPageCount = 2,
                        ) { currentPage ->
                            val shouldRender = abs(pagerState.currentPage - currentPage) <= 1
                            if (shouldRender && currentPage < viewModel.quranPageModels.size) {
                                QuranPageItem(
                                    context = context,
                                    viewModel = viewModel,
                                    currentPage = currentPage,
                                    fontColor = fontColor,
                                    suraHeaderColor = suraHeaderColor,
                                    suraNameColor = suraNameColor,
                                    highlightColor = highlightColor,
                                    ayaNumberInSuraToHighlight = ayaNumberInSuraToHighlight,
                                    surahIdToHighlight = surahIdToHighlight,
                                    bookmarkedAyas = bookmarkedAyas,
                                    bookmarkHighlightColor = bookmarkHighlightColor,
                                    errorWordLocations = errorWordLocations,
                                    errorHighlightColor = errorHighlightColor,
                                    ayahNumberColor = ayahNumberColor,
                                    highlightType = highlightType,
                                    isSurahClickable = isSurahClickable,
                                    isJuzClickable = isJuzClickable,
                                    isFontBold = isFontBold,
                                    onClickJuzName = onClickJuzName,
                                    onClickSurahName = onClickSurahName,
                                    onWordLongPressed = onWordLongPressed,
                                    onWordClicked = onWordClicked,
                                    onPageTap = onPageTap,
                                )
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

// =============================================================================
// QuranPageItem — header + canvas body + footer
// =============================================================================
@Composable
private fun QuranPageItem(
    context: Context,
    viewModel: QuranViewModel,
    currentPage: Int,
    fontColor: Color,
    suraHeaderColor: Color,
    suraNameColor: Color,
    highlightColor: Color,
    ayaNumberInSuraToHighlight: Int,
    surahIdToHighlight: Int,
    bookmarkedAyas: List<Pair<Int, Int>>,
    bookmarkHighlightColor: Color,
    errorWordLocations: Set<String>,
    errorHighlightColor: Color,
    ayahNumberColor: Color,
    highlightType: Int,
    isSurahClickable: Boolean,
    isJuzClickable: Boolean,
    isFontBold: Boolean,
    onClickJuzName: (ChapterModel) -> Unit,
    onClickSurahName: (SurahModel) -> Unit,
    onWordLongPressed: (Int, WordModel, RectF, Int?, List<WordModel>, Int) -> Unit,
    onWordClicked: (WordModel, Int) -> Unit,
    onPageTap: () -> Unit,
) {
    val pageModel = remember(currentPage) { viewModel.quranPageModels[currentPage] }
    val pageNumber = remember(currentPage) { toArabicNumber(currentPage + 1) }
    val density = LocalDensity.current

    // Track font readiness so the composable recomposes when the font finishes
    // loading — without this, the page stays blank even after the font is ready.
    val fontReady = viewModel.fontReadyState[currentPage + 1]
    val typeface = remember(currentPage, fontReady) {
        viewModel.getTypefaceForPage(context, currentPage + 1)
    }
    val typefaceSuraName = viewModel.typefaceSuraName
    val suraHeaderBitmap = viewModel.getBitmap(context, R.drawable.surah_title)
    val basmalaBitmap = viewModel.getBitmap(context, R.drawable.basmala)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = pageModel.chapterModel.name_ar ?: "",
                color = fontColor,
                fontSize = 14.toSP,
                fontFamily = amiri_quran,
                textAlign = TextAlign.Center,
                textDecoration = if (isJuzClickable) TextDecoration.Underline else TextDecoration.None,
                modifier = Modifier.clickable {
                    if (isJuzClickable) onClickJuzName(
                        pageModel.chapterModel
                    )
                },
            )
            Text(
                text = pageModel.surahModel.name_ar ?: "",
                color = fontColor,
                fontSize = 14.toSP,
                fontFamily = amiri_quran,
                textAlign = TextAlign.Center,
                textDecoration = if (isSurahClickable) TextDecoration.Underline else TextDecoration.None,
                modifier = Modifier.clickable {
                    if (isSurahClickable) onClickSurahName(
                        pageModel.surahModel
                    )
                },
            )
        }

        if (typeface != null && typefaceSuraName != null && suraHeaderBitmap != null && basmalaBitmap != null) {
            val textSizePx = with(density) { 20.dp.toPx() }
            val horizontalPaddingPx = with(density) { 10.dp.toPx() }
            CanvasQuranPage(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                horizontalPaddingPx = horizontalPaddingPx,
                context = context,
                pageModel = pageModel,
                typeface = typeface,
                typefaceSuraName = typefaceSuraName,
                suraHeaderBitmap = suraHeaderBitmap,
                basmalaBitmap = basmalaBitmap,
                textSizePx = textSizePx,
                fontColorArgb = fontColor.toArgb(),
                highlightColorArgb = highlightColor.toArgb(),
                ayaNumberInSuraToHighlight = ayaNumberInSuraToHighlight,
                surahIdToHighlight = surahIdToHighlight,
                bookmarkedAyaSet = remember(bookmarkedAyas) {
                    bookmarkedAyas.mapTo(HashSet()) { (s, a) -> s.toLong() * 10_000L + a }
                },
                bookmarkHighlightColorArgb = bookmarkHighlightColor.toArgb(),
                errorWordLocations = errorWordLocations,
                errorHighlightColorArgb = errorHighlightColor.toArgb(),
                ayahNumberColorArgb = ayahNumberColor.toArgb(),
                suraHeaderColor = suraHeaderColor,
                suraNameColor = suraNameColor,
                isBold = isFontBold,
                highlightType = highlightType,
                // Wrap to inject the page number (1-based) into the callbacks
                onWordLongPressed = { ht, wm, rect, ayah, words ->
                    onWordLongPressed(ht, wm, rect, ayah, words, currentPage + 1)
                },
                onWordClicked = { wm -> onWordClicked(wm, currentPage + 1) },
                onPageTap = onPageTap,
            )
        } else {
            // Font not ready yet — show a subtle loading indicator instead of
            // leaving the page area blank/white.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = suraHeaderColor,
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 2.dp,
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
}

// =============================================================================
// CanvasQuranPage
// =============================================================================
@Composable
private fun CanvasQuranPage(
    modifier: Modifier,
    horizontalPaddingPx: Float,
    context: Context,
    pageModel: QuranPageModel,
    typeface: Typeface,
    typefaceSuraName: Typeface,
    suraHeaderBitmap: Bitmap,
    basmalaBitmap: Bitmap,
    textSizePx: Float,
    fontColorArgb: Int,
    highlightColorArgb: Int,
    ayaNumberInSuraToHighlight: Int,
    surahIdToHighlight: Int,
    bookmarkedAyaSet: Set<Long>,
    bookmarkHighlightColorArgb: Int,
    errorWordLocations: Set<String>,
    errorHighlightColorArgb: Int,
    ayahNumberColorArgb: Int,
    suraHeaderColor: Color,
    suraNameColor: Color,
    isBold: Boolean,
    highlightType: Int,
    onWordLongPressed: (Int, WordModel, RectF, Int?, List<WordModel>) -> Unit,
    onWordClicked: (WordModel) -> Unit,
    onPageTap: () -> Unit,
) {
    var selectedWord by remember(pageModel) { mutableStateOf<WordModel?>(null) }
    var selectedAyah by remember(pageModel) { mutableStateOf<Int?>(null) }
    var selectedSurah by remember(pageModel) { mutableStateOf<Int?>(null) }
    // موقع الكلمة المحددة في الـ window الحقيقي بالـ px
    var selectedWordRectInWindow by remember { mutableStateOf(RectF()) }

    // موقع الـ Canvas في الـ window
    var canvasWindowOffset by remember { mutableStateOf(Offset.Zero) }

    val wordRects = remember(pageModel) { mutableListOf<Pair<WordModel, RectF>>() }

    val textPaint = remember(typeface, textSizePx, fontColorArgb, isBold) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            this.textSize = textSizePx
            this.color = fontColorArgb
            this.textAlign = Paint.Align.LEFT
            isSubpixelText = true
            if (isBold) setShadowLayer(0f, 0.9f, 0.9f, fontColorArgb)
        }
    }

    val wordsByAyah = remember(pageModel) {
        pageModel.lines.flatMap { it.words }.groupBy { it.surahId to it.ayah }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coords ->
                    // نسجل موقع الـ Canvas في الـ window مرة واحدة
                    canvasWindowOffset = coords.positionInWindow()
                }
                .pointerInput(pageModel, highlightType) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            selectedWord = null
                            selectedAyah = null
                            selectedSurah = null
                            val hit = wordRects.firstOrNull { (_, rect) ->
                                rect.contains(tapOffset.x, tapOffset.y)
                            }?.first
                            if (hit != null) {
                                onWordClicked(hit)
                            }
                            // Always notify so the host can clear any external highlight
                            // (e.g. a search-result highlight) when the user taps the page.
                            onPageTap()
                        },
                        onLongPress = { tapOffset ->
                            val hit = wordRects.firstOrNull { (_, rect) ->
                                rect.contains(tapOffset.x, tapOffset.y)
                            }?.first

                            if (hit != null) {
                                if (highlightType == QuranConstants.HIGHLIGHT_TYPE_AYA) {
                                    selectedAyah = hit.ayah
                                    selectedWord = null
                                } else {
                                    selectedWord = hit
                                    selectedAyah = null
                                }
                                selectedSurah = hit.surahId

                                // نحوّل موقع الكلمة من Canvas-local إلى Window
                                val hitRect = wordRects.firstOrNull { it.first == hit }?.second
                                if (hitRect != null) {
                                    selectedWordRectInWindow = RectF(
                                        canvasWindowOffset.x + hitRect.left,
                                        canvasWindowOffset.y + hitRect.top,
                                        canvasWindowOffset.x + hitRect.right,
                                        canvasWindowOffset.y + hitRect.bottom,
                                    )
                                }
                                onWordLongPressed(
                                    highlightType,
                                    hit,
                                    selectedWordRectInWindow,
                                    selectedAyah,
                                    wordsByAyah[selectedSurah to selectedAyah] ?: emptyList()
                                )
//                                showContextMenu = true
                            }
                        },
                    )
                },
        ) {
            wordRects.clear()
            drawPageContent(
                scope = this,
                horizontalPaddingPx = horizontalPaddingPx,
                pageModel = pageModel,
                textPaint = textPaint,
                typefaceSuraName = typefaceSuraName,
                suraHeaderBitmap = suraHeaderBitmap,
                basmalaBitmap = basmalaBitmap,
                textSizePx = textSizePx,
                fontColorArgb = fontColorArgb,
                highlightColorArgb = highlightColorArgb,
                ayaNumberInSuraToHighlight = ayaNumberInSuraToHighlight,
                surahIdToHighlight = surahIdToHighlight,
                bookmarkedAyaSet = bookmarkedAyaSet,
                bookmarkHighlightColorArgb = bookmarkHighlightColorArgb,
                errorWordLocations = errorWordLocations,
                errorHighlightColorArgb = errorHighlightColorArgb,
                ayahNumberColorArgb = ayahNumberColorArgb,
                suraHeaderColor = suraHeaderColor,
                suraNameColor = suraNameColor,
                selectedWord = selectedWord,
                selectedAyah = selectedAyah,
                selectedSurah = selectedSurah,
                highlightType = highlightType,
                wordRectsOut = wordRects,
            )
        }
    }
}

private fun drawPageContent(
    scope: DrawScope,
    horizontalPaddingPx: Float,
    pageModel: QuranPageModel,
    textPaint: Paint,
    typefaceSuraName: Typeface,
    suraHeaderBitmap: Bitmap,
    basmalaBitmap: Bitmap,
    textSizePx: Float,
    fontColorArgb: Int,
    highlightColorArgb: Int,
    ayaNumberInSuraToHighlight: Int,
    surahIdToHighlight: Int,
    bookmarkedAyaSet: Set<Long>,
    bookmarkHighlightColorArgb: Int,
    errorWordLocations: Set<String>,
    errorHighlightColorArgb: Int,
    ayahNumberColorArgb: Int,
    suraHeaderColor: Color,
    suraNameColor: Color,
    selectedWord: WordModel?,
    selectedAyah: Int?,
    selectedSurah: Int?,
    highlightType: Int,
    wordRectsOut: MutableList<Pair<WordModel, RectF>>,
) {
    val canvasWidth = scope.size.width
    val canvasHeight = scope.size.height
    val lines = pageModel.lines
    if (lines.isEmpty()) return

    val lineHeight = canvasHeight / MUSHAF_LINES_PER_PAGE
    val lineCount = lines.size
    val blockHeight = lineHeight * lineCount
    val topOffset = (canvasHeight - blockHeight) / 2f

    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    scope.drawIntoCanvas { canvas ->
        val nativeCanvas = canvas.nativeCanvas

        lines.forEachIndexed { lineIdx, line ->
            val lineTop = topOffset + lineIdx * lineHeight
            val baseline = lineTop + lineHeight * 0.78f

            when (line.line_type) {

                LineModel.LINE_TYPE_SURAH_NAME -> {
                    val bitmapRatio = suraHeaderBitmap.width.toFloat() / suraHeaderBitmap.height
                    val drawW = canvasWidth + horizontalPaddingPx * 2f
                    val drawH = drawW / bitmapRatio
                    val drawLeft = -horizontalPaddingPx
                    val drawTop = lineTop + (lineHeight - drawH) / 2f
                    val destRect = RectF(drawLeft, drawTop, drawLeft + drawW, drawTop + drawH)
                    bitmapPaint.colorFilter = PorterDuffColorFilter(
                        suraHeaderColor.toArgb(), PorterDuff.Mode.SRC_IN,
                    )
                    nativeCanvas.drawBitmap(
                        suraHeaderBitmap, null, destRect, bitmapPaint
                    )

                    val surahPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        typeface = typefaceSuraName
                        textSize = textSizePx * 1.4f
                        color = suraNameColor.toArgb()
                        textAlign = Paint.Align.CENTER
                    }
                    val textY =
                        destRect.top + destRect.height() / 2f - (surahPaint.descent() + surahPaint.ascent()) / 2f
                    nativeCanvas.drawText(
                        line.surahLigature ?: "",
                        canvasWidth / 2f,
                        textY,
                        surahPaint,
                    )
                }

                LineModel.LINE_TYPE_BASMALAH -> {
                    val ratio = basmalaBitmap.width.toFloat() / basmalaBitmap.height
                    val drawH = lineHeight * 0.85f
                    val drawW = (drawH * ratio).coerceAtMost(canvasWidth * 0.8f)
                    val finalH = drawW / ratio
                    val drawLeft = (canvasWidth - drawW) / 2f
                    val drawTop = lineTop + (lineHeight - finalH) / 2f

                    bitmapPaint.colorFilter =
                        PorterDuffColorFilter(fontColorArgb, PorterDuff.Mode.SRC_IN)
                    nativeCanvas.drawBitmap(
                        basmalaBitmap,
                        null,
                        RectF(drawLeft, drawTop, drawLeft + drawW, drawTop + finalH),
                        bitmapPaint,
                    )
                }

                else -> {
                    val words = line.words
                    if (words.isEmpty()) return@forEachIndexed

                    val wordMetrics = Array(words.size) { i ->
                        val bounds = android.graphics.Rect()
                        textPaint.getTextBounds(
                            words[i].glyph, 0, words[i].glyph.length, bounds
                        )
                        val advance = measureWordWidth(textPaint, words[i].glyph)
                        val boundsRight = bounds.right.toFloat()
                        val visualWidth = if (boundsRight > advance * 3f) {
                            boundsRight
                        } else {
                            advance.coerceAtLeast(boundsRight)
                        }
                        Pair(visualWidth, 0f)
                    }

                    val totalVisualWidth = wordMetrics.sumOf { it.first.toDouble() }.toFloat()
                    val count = words.size
                    val minGapTotal = if (count > 1) WORD_GAP_PX * (count - 1) else 0f

                    val scaleX = if (totalVisualWidth + minGapTotal > canvasWidth) {
                        canvasWidth / (totalVisualWidth + minGapTotal)
                    } else {
                        1f
                    }

                    val virtualWidth = canvasWidth / scaleX
                    val xPositions =
                        computeWordPositions(virtualWidth, line, textPaint, wordMetrics)

                    nativeCanvas.save()
                    if (scaleX < 1f) {
                        nativeCanvas.scale(scaleX, 1f, canvasWidth, baseline)
                    }

                    // ── Bookmark highlights (always visible, drawn first) ──────────────
                    if (bookmarkedAyaSet.isNotEmpty()) {
                        val bmGroups = mutableMapOf<Long, Pair<Float, Float>>()
                        words.forEachIndexed { i, word ->
                            val key = word.surahId.toLong() * 10_000L + word.ayah
                            if (key in bookmarkedAyaSet) {
                                val vw = wordMetrics[i].first
                                val cur = bmGroups[key]
                                bmGroups[key] = if (cur == null) {
                                    xPositions[i] to xPositions[i] + vw
                                } else {
                                    minOf(cur.first, xPositions[i]) to
                                            maxOf(cur.second, xPositions[i] + vw)
                                }
                            }
                        }
                        if (bmGroups.isNotEmpty()) {
                            fillPaint.color = bookmarkHighlightColorArgb
                            bmGroups.values.forEach { (l, r) ->
                                nativeCanvas.drawRect(
                                    l,
                                    lineTop,
                                    r,
                                    lineTop + lineHeight,
                                    fillPaint
                                )
                            }
                        }
                    }

                    if (ayaNumberInSuraToHighlight != -1 && surahIdToHighlight != -1) {
                        var mergedLeft = Float.MAX_VALUE
                        var mergedRight = -Float.MAX_VALUE
                        words.forEachIndexed { i, word ->
                            if (word.ayah == ayaNumberInSuraToHighlight && word.surahId == surahIdToHighlight) {
                                val vw = wordMetrics[i].first
                                if (xPositions[i] < mergedLeft) mergedLeft = xPositions[i]
                                if (xPositions[i] + vw > mergedRight) mergedRight =
                                    xPositions[i] + vw
                            }
                        }
                        if (mergedLeft != Float.MAX_VALUE) {
                            fillPaint.color = highlightColorArgb
                            nativeCanvas.drawRect(
                                mergedLeft,
                                lineTop,
                                mergedRight,
                                lineTop + lineHeight,
                                fillPaint,
                            )
                        }
                    } else if (highlightType == QuranConstants.HIGHLIGHT_TYPE_AYA) {
                        if (selectedAyah != null && selectedSurah != null) {
                            var mergedLeft = Float.MAX_VALUE
                            var mergedRight = -Float.MAX_VALUE
                            words.forEachIndexed { i, word ->
                                if (word.ayah == selectedAyah && word.surahId == selectedSurah) {
                                    val vw = wordMetrics[i].first
                                    if (xPositions[i] < mergedLeft) mergedLeft = xPositions[i]
                                    if (xPositions[i] + vw > mergedRight) mergedRight =
                                        xPositions[i] + vw
                                }
                            }
                            if (mergedLeft != Float.MAX_VALUE) {
                                fillPaint.color = highlightColorArgb
                                nativeCanvas.drawRect(
                                    mergedLeft,
                                    lineTop,
                                    mergedRight,
                                    lineTop + lineHeight,
                                    fillPaint,
                                )
                            }
                        }
                    } else {
                        if (selectedWord != null) {
                            words.forEachIndexed { i, word ->
                                if (word.location == selectedWord.location) {
                                    val vw = wordMetrics[i].first
                                    fillPaint.color = highlightColorArgb
                                    nativeCanvas.drawRect(
                                        xPositions[i], lineTop,
                                        xPositions[i] + vw, lineTop + lineHeight,
                                        fillPaint,
                                    )
                                }
                            }
                        }
                    }

                    words.forEachIndexed { i, word ->
                        val x = xPositions[i]
                        val vw = wordMetrics[i].first
                        val isAyahNum =
                            word.wordText.isNotEmpty() && word.wordText.all { it in '٠'..'٩' }
                        val isErrorWord = errorWordLocations.isNotEmpty() &&
                                word.location in errorWordLocations
                        when {
                            isAyahNum   -> textPaint.color = ayahNumberColorArgb
                            isErrorWord -> textPaint.color = errorHighlightColorArgb
                        }
                        nativeCanvas.drawText(word.glyph, x, baseline, textPaint)
                        if (isAyahNum || isErrorWord) textPaint.color = fontColorArgb

                        val scaledLeft = canvasWidth - (canvasWidth - x) * scaleX
                        val scaledRight = canvasWidth - (canvasWidth - (x + vw)) * scaleX
                        wordRectsOut.add(
                            word to RectF(
                                scaledLeft, lineTop, scaledRight, lineTop + lineHeight
                            )
                        )
                    }

                    nativeCanvas.restore()
                }
            }
        }
    }
}

// =============================================================================
// computeWordPositions
// =============================================================================
private fun computeWordPositions(
    canvasWidth: Float,
    line: LineModel,
    textPaint: Paint,
    wordMetrics: Array<Pair<Float, Float>>,
): FloatArray {
    val words = line.words
    val count = words.size
    if (count == 0) return FloatArray(0)

    val visualWidths = FloatArray(count) { wordMetrics[it].first }
    val totalVisualWidth = visualWidths.sum()
    val positions = FloatArray(count)

    if (line.isCentered || count == 1) {
        val totalWithGaps = totalVisualWidth + (count - 1).coerceAtLeast(0) * WORD_GAP_PX
        var x = (canvasWidth + totalWithGaps) / 2f
        for (i in 0 until count) {
            x -= visualWidths[i]
            positions[i] = x
            if (i < count - 1) x -= WORD_GAP_PX
        }
    } else {
        val extraSpace = (canvasWidth - totalVisualWidth).coerceAtLeast(0f)
        val gap = if (count > 1) extraSpace / (count - 1) else 0f
        var x = canvasWidth - visualWidths[0]
        for (i in 0 until count) {
            positions[i] = x
            if (i < count - 1) {
                x -= visualWidths[i + 1] + gap
            }
        }
    }

    return positions
}

// =============================================================================
// WordMenuPositionProvider
// بياخد موقع الكلمة في الـ window ويحسب موقع المنيو فوقها أو تحتها
// الـ Popup بيستدعي calculatePosition بعد ما يحسب حجمه الفعلي
// =============================================================================
@Stable
class WordMenuPositionProvider(
    private val wordRectInWindow: android.graphics.RectF,
    private val menuMarginPx: Int,
    private val screenWidthPx: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val menuH = popupContentSize.height
        val menuW = popupContentSize.width

        val wordTop = wordRectInWindow.top.toInt()
        val wordBottom = wordRectInWindow.bottom.toInt()
        val wordLeft = wordRectInWindow.left.toInt()

        // فوق الكلمة لو في مكان كافي، وإلا تحتها
        val y = if (wordTop - menuH - menuMarginPx >= 0) {
            wordTop - menuH - menuMarginPx
        } else {
            wordBottom + menuMarginPx
        }

        // X: نبدأ من يسار الكلمة مع تجنب الخروج من الشاشة
        val x = wordLeft.coerceIn(0, (screenWidthPx - menuW).coerceAtLeast(0))

        return IntOffset(x, y)
    }
}