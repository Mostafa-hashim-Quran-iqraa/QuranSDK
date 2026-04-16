package com.blacksmith.quranlib.presentation.newQuranScreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksmith.quranlib.R
import com.blacksmith.quranlib.data.model.AyaModel
import com.blacksmith.quranlib.data.model.ChapterModel
import com.blacksmith.quranlib.data.model.LineModel
import com.blacksmith.quranlib.data.model.QuranPageModel
import com.blacksmith.quranlib.data.model.RenderLine
import com.blacksmith.quranlib.data.model.RenderWord
import com.blacksmith.quranlib.data.model.SurahModel
import com.blacksmith.quranlib.data.model.WordModel
import com.blacksmith.quranlib.domain.remote.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.graphics.createBitmap

@HiltViewModel
class QuranViewModel @Inject constructor(
    var quranRepository: QuranRepository,
) : ViewModel() {

    var isDataLoaded by mutableStateOf(false)
        private set
    var isShowLoader by mutableStateOf(true)
        private set
    var isShowError by mutableStateOf(false)
        private set
    var quranPageModels = mutableStateListOf<QuranPageModel>()
        private set

    private val _typefaceCache = object : LinkedHashMap<String, Typeface>(
        MAX_CACHED_FONTS + 1, 0.75f, true
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Typeface>): Boolean =
            size > MAX_CACHED_FONTS
    }
    var typefaceSuraName: Typeface? = null
    private val bitmapCache = mutableMapOf<Int, Bitmap>()
    private val _fontCacheLock = Any()

    var fontReadyState = mutableStateMapOf<Int, Boolean>()
        private set

    private var _dataVersion = 0
    private val _layoutComputedPages = mutableSetOf<String>()
    private val _layoutLock = Any()

    // Cache للـ glyph widths المقاسة بالـ bitmap — key = "fontKey|text|textSizeInt"
    // Samsung بيكذب على كل الـ APIs (measureText, getTextBounds, getTextPath, getRunAdvance)
    // بشكل عشوائي لبعض الـ PUA glyphs، فالحل الوحيد الموثوق هو عد الـ pixels.
    private val _glyphWidthCache = mutableMapOf<String, Float>()
    private val _glyphCacheLock = Any()

    companion object {
        val EMPTY_SURAH = SurahModel()
        val EMPTY_CHAPTER = ChapterModel()
        val EMPTY_AYA = AyaModel()
        const val TOTAL_PAGES = 604
        private const val MAX_CACHED_FONTS = 30
        const val MUSHAF_LINES_PER_PAGE = 16

        // Samsung S25 Ultra: textSize=75px
        // أكبر كلمة طبيعية = 278px = 3.71×ts
        // أصغر over-report مؤكد = 436px = 5.81×ts
        // threshold = 4.5× يفصل بينهم بأمان
        private const val UPPER_THRESHOLD_RATIO = 4.5f
        private const val LOWER_THRESHOLD_RATIO = 0.4f

        // حجم الـ bitmap المؤقت للقياس (بالـ textSize units)
        private const val BITMAP_WIDTH_RATIO = 8
        private const val BITMAP_HEIGHT_RATIO = 2
    }

    // ─── Font helpers ─────────────────────────────────────────────────────────
    fun getTypefaceForPage(context: Context, page: Int): Typeface? {
        val key = fontFileNameForPage(page)
        val cached = synchronized(_fontCacheLock) { _typefaceCache[key] }
        if (cached != null) return cached

        viewModelScope.launch(Dispatchers.IO) {
            val typeface = loadTypefaceFromAssets(context, key)
            synchronized(_fontCacheLock) { _typefaceCache[key] = typeface }
            withContext(Dispatchers.Main) { fontReadyState[page] = true }
        }
        return null
    }

    fun loadBitmap(context: Context, @DrawableRes resId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, resId)!!
        val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun getBitmap(context: Context, @DrawableRes resId: Int): Bitmap =
        bitmapCache.getOrPut(resId) { loadBitmap(context, resId) }

    fun preloadFontsAround(context: Context, currentPage: Int, range: Int = 5) {
        val start = maxOf(1, currentPage - range)
        val end = minOf(TOTAL_PAGES, currentPage + range)
        viewModelScope.launch(Dispatchers.IO) {
            for (page in start..end) {
                val key = fontFileNameForPage(page)
                val alreadyCached = synchronized(_fontCacheLock) { _typefaceCache.containsKey(key) }
                if (alreadyCached) continue
                val typeface = loadTypefaceFromAssets(context, key)
                synchronized(_fontCacheLock) { _typefaceCache[key] = typeface }
                withContext(Dispatchers.Main) { fontReadyState[page] = true }
            }
        }
    }

    fun loadTypefaceFromAssets(context: Context, fontFileName: String): Typeface =
        Typeface.createFromAsset(context.assets, "fonts/$fontFileName")

    fun loadTypefaceFromRes(context: Context, fontResId: Int): Typeface? =
        ResourcesCompat.getFont(context, fontResId)

    private fun fontFileNameForPage(page: Int): String {
        val p = page.toString().padStart(3, '0')
        return "QCF2$p.ttf"
    }

    // ─── Data loading ─────────────────────────────────────────────────────────
    fun getData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                isShowLoader = true
                isShowError = false
                isDataLoaded = false
            }
            quranPageModels.clear()
            synchronized(_fontCacheLock) { _typefaceCache.clear() }
            synchronized(_layoutLock) {
                _layoutComputedPages.clear()
                _dataVersion++
            }
            synchronized(_glyphCacheLock) { _glyphWidthCache.clear() }
            fontReadyState.clear()
            typefaceSuraName = loadTypefaceFromRes(context, R.font.surah_name_v4)

            try {
                val quranDeferred = async { quranRepository.getQuranData(context) }
                val pagesDeferred = async { quranRepository.getPages() }
                val wordsDeferred = async { quranRepository.getWords() }
                val wordsTextDeferred = async { quranRepository.getWordsText() }

                val quranFileResponseModel = quranDeferred.await()
                val pages = pagesDeferred.await()
                val words = wordsDeferred.await()
                val wordsText = wordsTextDeferred.await()

                val wordsMap = words.associateBy { it.id }
                val wordsTextMap = wordsText.associateBy { it.id }
                val pagesGrouped = pages.groupBy { it.page_number }

                val surahMap = quranFileResponseModel.suras
                    ?.associateBy { it.id!!.toInt() } ?: emptyMap()
                val chapterMap = quranFileResponseModel.chapters
                    ?.associateBy { it.id!!.toInt() } ?: emptyMap()

                val ayasMap: Map<Int, Map<Int, AyaModel>> = surahMap.mapValues { (_, surah) ->
                    surah.ayas?.associateBy { it.id!!.toInt() } ?: emptyMap()
                }

                val newPages = buildList {
                    for ((pageNumber, lines) in pagesGrouped) {
                        var lineSurahModel = EMPTY_SURAH
                        var lineChapterModel = EMPTY_CHAPTER

                        val lineModels = lines.sortedBy { it.line_number }.map { line ->
                            lineSurahModel = surahMap[line.surah_number] ?: EMPTY_SURAH
                            lineChapterModel = chapterMap[line.chapter_number] ?: EMPTY_CHAPTER

                            val wordList = (line.first_word_id!!..line.last_word_id!!)
                                .mapNotNull { id ->
                                    val word = wordsMap[id] ?: return@mapNotNull null
                                    val wordSurahModel = surahMap[word.surah] ?: EMPTY_SURAH
                                    val ayaModel = ayasMap[word.surah]?.get(word.ayah) ?: EMPTY_AYA
                                    val wordChapterModel =
                                        chapterMap[ayaModel.chapter_id] ?: EMPTY_CHAPTER
                                    WordModel(
                                        id = word.id,
                                        text = word.text ?: "",
                                        wordText = wordsTextMap[id]?.text ?: "",
                                        location = word.location,
                                        surahId = word.surah ?: 0,
                                        surahName = wordSurahModel.name_ar ?: "",
                                        chapterId = wordChapterModel.id?.toIntOrNull() ?: 0,
                                        chapterName = wordChapterModel.name_ar ?: "",
                                        ayah = word.ayah ?: 0,
                                        word = word.word ?: 0,
                                    )
                                }

                            LineModel(
                                lineNumber = line.line_number,
                                isCentered = (line.is_centered == 1),
                                surahId = lineSurahModel.id!!.toInt(),
                                surahName = lineSurahModel.name_ar ?: "",
                                surahLigature = lineSurahModel.ligature,
                                chapterId = lineChapterModel.id?.toIntOrNull() ?: 0,
                                chapterName = lineChapterModel.name_ar ?: "",
                                line_type = line.line_type,
                                words = wordList,
                            )
                        }

                        add(
                            QuranPageModel(
                                pageNumber = pageNumber,
                                surahModel = lineSurahModel,
                                lines = lineModels,
                                chapterModel = lineChapterModel,
                            )
                        )
                    }
                }

                withContext(Dispatchers.Main) {
                    quranPageModels.addAll(newPages)
                    isShowLoader = false
                    isShowError = false
                    isDataLoaded = true
                }

                preloadFontsAround(context, currentPage = 1, range = 5)

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isShowLoader = false
                    isShowError = true
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        synchronized(_fontCacheLock) { _typefaceCache.clear() }
        synchronized(_layoutLock) { _layoutComputedPages.clear() }
        synchronized(_glyphCacheLock) { _glyphWidthCache.clear() }
    }
}