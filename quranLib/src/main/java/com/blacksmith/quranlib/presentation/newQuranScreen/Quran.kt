package com.blacksmith.quranlib.presentation.newQuranScreen

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

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

// ─── Constants ────────────────────────────────────────────────────────────────
private const val WORD_GAP_PX = 1f
private const val MUSHAF_LINES_PER_PAGE = 16

// =============================================================================
// QuranPageScreen — public entry point
// =============================================================================
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
    onClickJuzName: (ChapterModel) -> Unit = {},
    onClickSurahName: (SurahModel) -> Unit = {},
) {
    val context = LocalContext.current

    ComposableLifecycle { _, event ->
        if (event == Lifecycle.Event.ON_START && !viewModel.isDataLoaded) {
            viewModel.getData(context)
        }
    }

    val initialPage = pageToOpen.coerceIn(1, 604) - 1
    val pagerState = rememberPagerState(initialPage = initialPage) { 604 }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.preloadFontsAround(context, pagerState.currentPage + 1, range = 3)
    }

    QuranContent(
        context = context,
        pagerState = pagerState,
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
        isFontBold = isFontBold,
        onClickJuzName = onClickJuzName,
        onClickSurahName = onClickSurahName,
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
    onClickJuzName: (ChapterModel) -> Unit,
    onClickSurahName: (SurahModel) -> Unit,
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
                        onClick = { viewModel.getData(context) },
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
                                    isAyaHighlight = isAyaHighlight,
                                    isSurahClickable = isSurahClickable,
                                    isJuzClickable = isJuzClickable,
                                    isFontBold = isFontBold,
                                    onClickJuzName = onClickJuzName,
                                    onClickSurahName = onClickSurahName,
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
    isAyaHighlight: Boolean,
    isSurahClickable: Boolean,
    isJuzClickable: Boolean,
    isFontBold: Boolean,
    onClickJuzName: (ChapterModel) -> Unit,
    onClickSurahName: (SurahModel) -> Unit,
) {
    val pageModel = remember(currentPage) { viewModel.quranPageModels[currentPage] }
    val pageNumber = remember(currentPage) { toArabicNumber(currentPage + 1) }
    val density = LocalDensity.current

    @Suppress("UNUSED_VARIABLE")
    val fontReady = viewModel.fontReadyState[currentPage + 1]
    val typeface = viewModel.getTypefaceForPage(context, currentPage + 1)
    val typefaceSuraName = viewModel.typefaceSuraName
    val suraHeaderBitmap = viewModel.getBitmap(context, R.drawable.surah_title)
    val basmalaBitmap = viewModel.getBitmap(context, R.drawable.basmala)

    Column(
        modifier = Modifier.padding(horizontal = 10.dp).fillMaxSize(),
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
                modifier = Modifier.clickable { if (isJuzClickable) onClickJuzName(pageModel.chapterModel) },
            )
            Text(
                text = pageModel.surahModel.name_ar ?: "",
                color = fontColor,
                fontSize = 14.toSP,
                fontFamily = amiri_quran,
                textAlign = TextAlign.Center,
                textDecoration = if (isSurahClickable) TextDecoration.Underline else TextDecoration.None,
                modifier = Modifier.clickable { if (isSurahClickable) onClickSurahName(pageModel.surahModel) },
            )
        }

        if (typeface != null && typefaceSuraName != null &&
            suraHeaderBitmap != null && basmalaBitmap != null
        ) {
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
                suraHeaderColor = suraHeaderColor,
                suraNameColor = suraNameColor,
                isBold = isFontBold,
                isAyaHighlight = isAyaHighlight,
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
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
    suraHeaderColor: Color,
    suraNameColor: Color,
    isBold: Boolean,
    isAyaHighlight: Boolean,
) {
    var selectedWord by remember(pageModel) { mutableStateOf<WordModel?>(null) }
    var selectedAyah by remember(pageModel) { mutableStateOf<Int?>(null) }
    var selectedSurah by remember(pageModel) { mutableStateOf<Int?>(null) }
    var showContextMenu by remember { mutableStateOf(false) }
    var menuAnchorOffset by remember { mutableStateOf(Offset.Zero) }

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

    val selectedText = remember(selectedWord, selectedAyah, selectedSurah, isAyaHighlight) {
        if (isAyaHighlight && selectedAyah != null) {
            val ayahWords = wordsByAyah[selectedSurah to selectedAyah] ?: emptyList()
            "${ayahWords.joinToString(" ") { it.wordText }}\nسورة ${ayahWords.firstOrNull()?.surahName} - آية $selectedAyah"
        } else {
            selectedWord?.let { "${it.wordText}\nسورة ${it.surahName} - آية ${it.ayah}" } ?: ""
        }
    }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(pageModel, isAyaHighlight) {
                    detectTapGestures(
                        onTap = {
                            selectedWord = null
                            selectedAyah = null
                            selectedSurah = null
                            showContextMenu = false
                        },
                        onLongPress = { tapOffset ->
                            val hit = wordRects.firstOrNull { (_, rect) ->
                                rect.contains(tapOffset.x, tapOffset.y)
                            }?.first
                            if (hit != null) {
                                if (isAyaHighlight) {
                                    selectedAyah = hit.ayah
                                    selectedWord = null
                                } else {
                                    selectedWord = hit
                                    selectedAyah = null
                                }
                                selectedSurah = hit.surahId
                                val hitRect = wordRects.firstOrNull { it.first == hit }?.second
                                if (hitRect != null) {
                                    menuAnchorOffset = Offset(hitRect.left, hitRect.top)
                                }
                                showContextMenu = true
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
                suraHeaderColor = suraHeaderColor,
                suraNameColor = suraNameColor,
                selectedWord = selectedWord,
                selectedAyah = selectedAyah,
                selectedSurah = selectedSurah,
                isAyaHighlight = isAyaHighlight,
                wordRectsOut = wordRects,
            )
        }

        AnimatedVisibility(
            visible = showContextMenu && (selectedWord != null || selectedAyah != null),
        ) {
            Popup(
                offset = IntOffset(
                    menuAnchorOffset.x.roundToInt(),
                    menuAnchorOffset.y.roundToInt(),
                ),
                onDismissRequest = {
                    showContextMenu = false
                    selectedWord = null
                    selectedAyah = null
                },
            ) {
                QuranContextMenu(
                    onCopy = {
                        val cb =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cb.setPrimaryClip(ClipData.newPlainText("quran", selectedText))
                        showContextMenu = false
                        selectedWord = null
                        selectedAyah = null
                    },
                    onShare = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, selectedText)
                        }
                        context.startActivity(Intent.createChooser(intent, null))
                        showContextMenu = false
                        selectedWord = null
                        selectedAyah = null
                    },
                )
            }
        }
    }
}

// =============================================================================
// drawPageContent
// =============================================================================
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
    suraHeaderColor: Color,
    suraNameColor: Color,
    selectedWord: WordModel?,
    selectedAyah: Int?,
    selectedSurah: Int?,
    isAyaHighlight: Boolean,
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
                    // نمد الهيدر خارج الـ canvas بمقدار الـ padding الأفقي من كل جهة
                    // عشان يبدأ وينتهي مع حواف الشاشة مش مع حواف الـ canvas
                    val bitmapRatio = suraHeaderBitmap.width.toFloat() / suraHeaderBitmap.height
                    val drawW = canvasWidth + horizontalPaddingPx * 2f
                    val drawH = drawW / bitmapRatio
                    val drawLeft = -horizontalPaddingPx
                    val drawTop = lineTop + (lineHeight - drawH) / 2f
                    val destRect = RectF(drawLeft, drawTop, drawLeft + drawW, drawTop + drawH)
                    bitmapPaint.colorFilter = PorterDuffColorFilter(
                        suraHeaderColor.toArgb(), PorterDuff.Mode.SRC_IN,
                    )
                    nativeCanvas.drawBitmap(suraHeaderBitmap, null, destRect, bitmapPaint)

                    val surahPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        typeface = typefaceSuraName
                        textSize = textSizePx * 1.4f
                        color = suraNameColor.toArgb()
                        textAlign = Paint.Align.CENTER
                    }
                    val textY = destRect.top + destRect.height() / 2f -
                            (surahPaint.descent() + surahPaint.ascent()) / 2f
                    nativeCanvas.drawText(
                        line.surahLigature ?: "",
                        canvasWidth / 2f,
                        textY,
                        surahPaint,
                    )
                }

                LineModel.LINE_TYPE_BASMALAH -> {
                    // نحسب الحجم على أساس الـ lineHeight مش hPad ثابت
                    // عشان يشتغل صح على كل densities بما فيها S25 Ultra (density=3.75)
                    val ratio = basmalaBitmap.width.toFloat() / basmalaBitmap.height
                    // الارتفاع = 85% من lineHeight
                    val drawH = lineHeight * 0.85f
                    // العرض على أساس الـ ratio، بحد أقصى 70% من عرض الكانفاس
                    val drawW = (drawH * ratio).coerceAtMost(canvasWidth * 0.7f)
                    // نعيد حساب الارتفاع لو اتضغط العرض
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

                    // ── ROOT CAUSE FIX ────────────────────────────────────────
                    // الفونت القرآني ده عنده glyphs بـ advance width صغير جداً
                    // لكن الـ ink (bounds.right) أكبر بكتير.
                    // مثال: advance=34 لكن bounds.right=290
                    //
                    // ده معناه إن الـ glyph مصمم يتراكب على الكلمة اللي جنبه،
                    // لكن في الـ layout بتاعنا ده بيعمل overlap مرئي.
                    //
                    // الحل: نستخدم bounds.right كعرض فعلي لكل كلمة في حساب الـ
                    // layout والـ positions، مش الـ advance width.
                    // كده الـ justified gap بيتحسب على العرض البصري الحقيقي.
                    val wordMetrics = Array(words.size) { i ->
                        val bounds = android.graphics.Rect()
                        textPaint.getTextBounds(words[i].text, 0, words[i].text.length, bounds)
                        val advance = measureWordWidth(textPaint, words[i].text)
                        val boundsRight = bounds.right.toFloat()
                        // نستخدم bounds.right بس لو أكبر من advance بنسبة معقولة (< 4x)
                        // لو bounds.right أكبر بكتير (زي advance=8, bounds.right=264)
                        // ده glyph من النوع اللي بيتراكب عمداً — نستخدم bounds.right
                        // لو bounds.right أكبر بنسبة صغيرة أو أقل — نستخدم advance
                        // الفرق الكبير (> 3x) هو علامة الـ zero-width overlapping glyph
                        val visualWidth = if (boundsRight > advance * 3f) {
                            boundsRight
                        } else {
                            advance.coerceAtLeast(boundsRight)
                        }
                        Pair(visualWidth, 0f)
                    }

                    // العرض البصري الكلي للسطر (بدون gaps)
                    val totalVisualWidth = wordMetrics.sumOf { it.first.toDouble() }.toFloat()

                    val count = words.size
                    val minGapTotal = if (count > 1) WORD_GAP_PX * (count - 1) else 0f

                    // لو حتى بأدنى gap السطر أكبر من الكانفاس، نضغط الـ canvas
                    val scaleX = if (totalVisualWidth + minGapTotal > canvasWidth) {
                        canvasWidth / (totalVisualWidth + minGapTotal)
                    } else {
                        1f
                    }

                    // الـ positions تتحسب في الـ unscaled space
                    val virtualWidth = canvasWidth / scaleX
                    val xPositions = computeWordPositions(virtualWidth, line, textPaint, wordMetrics)

                    nativeCanvas.save()
                    if (scaleX < 1f) {
                        nativeCanvas.scale(scaleX, 1f, canvasWidth, baseline)
                    }

                    if (isAyaHighlight) {
                        if (selectedAyah != null && selectedSurah != null) {
                            var mergedLeft = Float.MAX_VALUE
                            var mergedRight = -Float.MAX_VALUE
                            words.forEachIndexed { i, word ->
                                if (word.ayah == selectedAyah && word.surahId == selectedSurah) {
                                    val vw = wordMetrics[i].first
                                    if (xPositions[i] < mergedLeft) mergedLeft = xPositions[i]
                                    if (xPositions[i] + vw > mergedRight) mergedRight = xPositions[i] + vw
                                }
                            }
                            if (mergedLeft != Float.MAX_VALUE) {
                                fillPaint.color = highlightColorArgb
                                nativeCanvas.drawRect(
                                    mergedLeft, lineTop, mergedRight, lineTop + lineHeight, fillPaint,
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
                        // drawX = x مباشرة بدون تعديل
                        // الـ negative left bearing في الفونت القرآني مقصود —
                        // الكلمة بتتراكب على اللي قبلها عشان تبان متصلة.
                        // إحنا بنستخدم inkWidth بس في حساب الـ layout (positions)
                        // عشان نحجز المساحة الصح، لكن نقطة الرسم تفضل كما هي.
                        nativeCanvas.drawText(word.text, x, baseline, textPaint)

                        // wordRects في screen space الحقيقي
                        val scaledLeft  = canvasWidth - (canvasWidth - x) * scaleX
                        val scaledRight = canvasWidth - (canvasWidth - (x + vw)) * scaleX
                        wordRectsOut.add(word to RectF(scaledLeft, lineTop, scaledRight, lineTop + lineHeight))
                    }

                    nativeCanvas.restore()
                }
            }
        }
    }
}

// =============================================================================
// computeWordPositions
// الآن بتاخد wordMetrics عشان تستخدم الـ visual width مش الـ advance
// =============================================================================
private fun computeWordPositions(
    canvasWidth: Float,
    line: LineModel,
    textPaint: Paint,
    wordMetrics: Array<Pair<Float, Float>>, // (visualWidth, leftBearing)
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
        // justified: extraSpace دايماً >= 0 لأن scaleX في drawPageContent بيضمن ده
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

// overload قديم للتوافق مع أي استدعاءات تانية
private fun computeWordPositions(
    canvasWidth: Float,
    line: LineModel,
    textPaint: Paint,
): FloatArray {
    val words = line.words
    val metrics = Array(words.size) { i ->
        val bounds = android.graphics.Rect()
        textPaint.getTextBounds(words[i].text, 0, words[i].text.length, bounds)
        val advance = measureWordWidth(textPaint, words[i].text)
        val boundsRight = bounds.right.toFloat()
        val visualWidth = if (boundsRight > advance * 3f) {
            boundsRight
        } else {
            advance.coerceAtLeast(boundsRight)
        }
        Pair(visualWidth, 0f)
    }
    return computeWordPositions(canvasWidth, line, textPaint, metrics)
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

// =============================================================================
// Utilities
// =============================================================================
fun toArabicNumber(number: Int): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return number.toString().map { arabicDigits[it - '0'] }.joinToString("")
}

fun measureWordWidth(textPaint: Paint, text: String): Float {
    if (text.isEmpty()) return 0f
    val widths = FloatArray(text.length)
    textPaint.getTextWidths(text, widths)
    return widths.sum()
}

fun Int.dpToPx(context: Context): Float =
    this * context.resources.displayMetrics.density