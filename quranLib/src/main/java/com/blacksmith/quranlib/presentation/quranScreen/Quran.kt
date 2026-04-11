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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.blacksmith.quranlib.R
import com.blacksmith.quranlib.data.model.LineModel
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

@Composable
fun QuranPageScreen(
    viewModel: QuranViewModel = hiltViewModel(),
    isReversePager: Boolean = false,
    pageBackground: Color = White,
    fontColor: Color = Black,
    suraHeaderColor: Color = GreenDark,
    suraNameColor: Color = GreenDark,
    highlightColor: Color = colorPrimaryMoreLight,
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
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 604 })

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
        highlightColor = highlightColor
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
    pageBackground: Color ,
    fontColor: Color ,
    suraHeaderColor: Color,
    suraNameColor: Color,
    highlightColor: Color ,
) {
    Surface(
        color = pageBackground, modifier = Modifier.fillMaxSize()
    ) {
        //content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackground), contentAlignment = Alignment.Center
        ) {

            //Loading
            if (viewModel.isShowLoader) {
                LoaderLottie(R.raw.loader_circle, colorPrimary, 40, 40)
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
                            state = pagerState, modifier = Modifier.fillMaxSize(),
                            //make direction always from right to left, so reverse layout in english locale
                            reverseLayout = isReversePager
                        ) { currentPage ->
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
                                    val chapterName =
                                        viewModel.quranPageModels[currentPage].chapterModel.name_ar
                                            ?: ""
                                    Text(
                                        text = chapterName,
                                        color = Black,
                                        fontSize = 14.toSP,
                                        fontFamily = amiri_quran,
                                        textAlign = TextAlign.Center
                                    )

                                    val surahName =
                                        viewModel.quranPageModels[currentPage].surahModel.name_ar
                                            ?: ""
                                    Text(
                                        text = surahName,
                                        color = Black,
                                        fontSize = 14.toSP,
                                        fontFamily = amiri_quran,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Page(
                                    context,
                                    viewModel,
                                    currentPage,
                                    fontColor = fontColor,
                                    suraHeaderColor = suraHeaderColor,
                                    suraNameColor = suraNameColor,
                                    highlightColor = highlightColor,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                                val pageNumber = toArabicNumber(currentPage + 1)
                                Text(
                                    color = Black,
                                    text = pageNumber,
                                    fontFamily = amiri_quran,
                                    fontSize = 14.toSP,
                                )
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
    fontColor: Color,
    suraHeaderColor: Color,
    suraNameColor: Color,
    highlightColor: Color ,
    modifier: Modifier
) {
//    val clipboard = LocalClipboard.current
    val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    val page = currentPage + 1
    val pageText = if (page < 10) "00$page" else if (page < 100) "0$page" else "$page"
    val fontFileName = "QCF2${pageText}.ttf"
    val quranFont = remember { loadTypefaceFromAssets(context, fontFileName) }
    val lines = viewModel.quranPageModels[currentPage].lines
    var selectedWord by remember { mutableStateOf<WordModel?>(null) }
    var selectedAyah by remember { mutableStateOf<Int?>(null) }
    var ayahRects by remember { mutableStateOf<Map<String, Rect>>(emptyMap()) }
    var showMenu by remember { mutableStateOf(false) }
    var menuOffset by remember { mutableStateOf(Offset.Zero) }
    val isHighlightFullAya = false
    Box(modifier = modifier) {
        FlowRow(
            modifier = Modifier
                .padding(horizontal = 10.toDP, vertical = 0.toDP)
                .fillMaxWidth()
                .fillMaxHeight(),
            maxLines = 17,
            maxItemsInEachRow = 1,
            horizontalArrangement = Arrangement.spacedBy(
                0.toDP, if (currentPage > 1) Alignment.Start else Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(
                5.toDP, Alignment.CenterVertically
            )
        ) {
            lines.forEachIndexed { index, line ->
                if (line.line_type == LineModel.LINE_TYPE_SURAH_NAME) {
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
                } else if (line.line_type == LineModel.LINE_TYPE_BASMALAH) {
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
                } else {
                    val words = line.words
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        horizontalArrangement = if (line.isCentered) {
                            Arrangement.Center
                        } else {
                            Arrangement.SpaceBetween
                        },
                        verticalArrangement = Arrangement.spacedBy(
                            0.toDP, Alignment.CenterVertically
                        )
                    ) {
                        words.forEachIndexed { index, word ->
                            val isSelected =
                                if (isHighlightFullAya)
                                    selectedAyah == word.ayah
                                else
                                    selectedWord?.location == word.location
                            Text(
                                color = fontColor,
                                fontWeight = FontWeight(900),
                                text = word.text,
                                fontFamily = quranFont,
                                fontSize = 21.toSP,
                                modifier = Modifier
                                    .background(if (isSelected) highlightColor else Color.Transparent)
                                    .onGloballyPositioned { coordinates ->
                                        val bounds = coordinates.boundsInRoot()
                                        if (isHighlightFullAya && selectedAyah == word.ayah) {
                                            ayahRects = ayahRects + (word.location!! to bounds)
                                        }
                                        if (isSelected) {
                                            val position = coordinates.positionInWindow()
                                            val wordTop = bounds.top
                                            val size = coordinates.size
                                            val screenWidth =
                                                Resources.getSystem().displayMetrics.widthPixels
                                            val menuWidth = 180f
                                            val menuHeight = 120f
                                            val mirroredX = screenWidth - position.x - size.width
                                            val x =
                                                if (mirroredX + size.width + menuWidth > screenWidth) {
                                                    mirroredX - menuWidth
                                                } else {
                                                    mirroredX + size.width
                                                }
                                            val y =
                                                wordTop - (size.height * 0.8f) - menuHeight - 12f
                                            menuOffset = Offset(x, y)
                                        }
                                    }
                                    .combinedClickable(onClick = {
                                        if (showMenu) return@combinedClickable
                                        selectedWord = null
                                        selectedAyah = null
                                        showMenu = false
                                    }, onLongClick = {
                                        if (isHighlightFullAya) {
                                            selectedAyah = word.ayah
                                            selectedWord = null
                                        } else {
                                            selectedWord = word
                                            selectedAyah = null
                                        }
                                        showMenu = true
                                    })
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = showMenu && (selectedWord != null || selectedAyah != null)) {
            Popup(
//                alignment = Alignment.TopEnd,
                offset = IntOffset(
                    menuOffset.x.toInt(),
                    menuOffset.y.toInt()
                ),
                onDismissRequest = {
                    showMenu = false
                    selectedWord = null
                }
            ) {
                Column(
                    modifier = Modifier
                        .background(White, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                ) {

                    // Copy
                    Text(
                        text = "Copy",
                        color = Black,
                        modifier = Modifier
                            .clickable {
                                val text = if (isHighlightFullAya && selectedAyah != null) {
                                    val allWords = viewModel.quranPageModels[currentPage].lines
                                        .flatMap { it.words }

                                    val ayahWords = allWords.filter { it.ayah == selectedAyah }

                                    val ayahText = ayahWords.joinToString(" ") { it.wordText }

                                    "$ayahText\nسورة ${ayahWords.first().surahName} - آية ${selectedAyah}"
                                } else {
                                    selectedWord?.let {
                                        "${it.wordText}\nسورة ${it.surahName} - آية ${it.ayah}"
                                    } ?: ""
                                }
                                val clipboard =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                                val clip = ClipData.newPlainText("text", text)
                                clipboard.setPrimaryClip(clip)
//                                viewModel.showBannerMessage(
//                                    BannerMessage(
//                                        text = text, isSuccess = true
//                                    )
//                                )

//                                selectedWord?.let { word ->
//                                    val text =
//                                        "${word.wordText}\nسورة ${word.surahName} - آية ${word.ayah}"
//                                }
                                showMenu = false
                                selectedWord = null
                                selectedAyah = null
                            }
                            .padding(12.dp)
                    )

                    // Share
                    Text(
                        text = "Share",
                        color = Black,
                        modifier = Modifier
                            .clickable {
                                val text = if (isHighlightFullAya && selectedAyah != null) {
                                    val allWords = viewModel.quranPageModels[currentPage].lines
                                        .flatMap { it.words }

                                    val ayahWords = allWords.filter { it.ayah == selectedAyah }

                                    val ayahText = ayahWords.joinToString(" ") { it.wordText }

                                    "$ayahText\nسورة ${ayahWords.first().surahName} - آية ${selectedAyah}"
                                } else {
                                    selectedWord?.let {
                                        "${it.wordText}\nسورة ${it.surahName} - آية ${it.ayah}"
                                    } ?: ""
                                }
//                                shareToOtherApps(context, text)
                                showMenu = false
                                selectedWord = null
                                selectedAyah = null
                            }
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Float.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }

fun findClosestWordIndex(
    offset: Offset, wordBounds: Map<Int, Rect>
): Int? {
    if (wordBounds.isEmpty()) return null

    return wordBounds.minByOrNull { (_, rect) ->
        val center = rect.center
        val dx = center.x - offset.x
        val dy = center.y - offset.y
        dx * dx + dy * dy
    }?.key
}

fun loadTypefaceFromAssets(context: Context, fontFileName: String): FontFamily {
    val typeface = Typeface.createFromAsset(context.assets, "fonts/$fontFileName")
    return FontFamily(typeface)
}

fun toArabicNumber(number: Int): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return number.toString().map { arabicDigits[it - '0'] }.joinToString("")
}