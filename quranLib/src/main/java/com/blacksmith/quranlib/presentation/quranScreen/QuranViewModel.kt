package com.blacksmith.quranlib.presentation.quranScreen

import android.content.Context
import android.graphics.Typeface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@HiltViewModel
class QuranViewModel @Inject constructor(
    var quranRepository: QuranRepository,
) : ViewModel()
{
    var isDataLoaded by mutableStateOf(false)
        private set
    var isShowLoader by mutableStateOf(true)
        private set
    var isShowError by mutableStateOf(false)
        private set
    var quranPageModels = mutableStateListOf<QuranPageModel>()
        private set
    private val _fontCache = mutableStateMapOf<String, FontFamily?>()

    init {
        quranPageModels.clear()
        _fontCache.clear()
    }


    companion object {
        private val EMPTY_SURAH = SurahModel()
        private val EMPTY_CHAPTER = ChapterModel()
        private val EMPTY_AYA = AyaModel()
        const val TOTAL_PAGES = 604
    }

    suspend fun preloadFontsAround(context: Context, currentPage: Int, range: Int = 3) {
        viewModelScope.launch(Dispatchers.IO) {
            val start = maxOf(1, currentPage - range)
            val end = minOf(TOTAL_PAGES, currentPage + range)
            for (page in start..end) {
                val fontFileName = fontFileNameForPage(page)
                if (!_fontCache.containsKey(fontFileName)) {
                    val typeface = loadTypefaceFromAssets(context, fontFileName)
                    withContext(Dispatchers.Main) {
                        _fontCache[fontFileName] = typeface
                    }
                }
            }
        }
    }

    fun getFontForPage(context: Context, page: Int): FontFamily? {
        val fontFileName = fontFileNameForPage(page)
        return _fontCache[fontFileName] ?: run {
            // Load immediately if not cached
            viewModelScope.launch(Dispatchers.IO) {
                val typeface = loadTypefaceFromAssets(context, fontFileName)
                withContext(Dispatchers.Main) {
                    _fontCache[fontFileName] = typeface
                }
            }
            null
        }
    }

    fun loadTypefaceFromAssets(context: Context, fontFileName: String): FontFamily {
        val typeface = Typeface.createFromAsset(context.assets, "fonts/$fontFileName")
        return FontFamily(typeface)
    }

    private fun fontFileNameForPage(page: Int): String {
        val pageText = when {
            page < 10 -> "00$page"
            page < 100 -> "0$page"
            else -> "$page"
        }
        return "QCF2${pageText}.ttf"
    }

    fun getData(context: Context,) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                isShowLoader = true
                isShowError = false
                isDataLoaded = false
            }
            quranPageModels.clear()
            _fontCache.clear()

            val quranDeferred = async { quranRepository.getQuranData(context) }
            val pagesDeferred = async { quranRepository.getPages() }
            val wordsDeferred = async { quranRepository.getWords() }
            val wordsTextDeferred = async { quranRepository.getWordsText() }
            // ✅ Preload fonts for first few pages right after data loads
            val fontsDeferred = async { preloadFontsAround(context, currentPage = 1, range = 604) }

            val quranFileResponseModel = quranDeferred.await()
            val pages = pagesDeferred.await()
            val words = wordsDeferred.await()
            val wordsText = wordsTextDeferred.await()
            val fonts = fontsDeferred.await()

            // ✅ كل الـ Maps اتبنت مرة واحدة
            val wordsMap = words.associateBy { it.id }
            val wordsTextMap = wordsText.associateBy { it.id }
            val pagesGrouped = pages.groupBy { it.page_number }

            val surahMap = quranFileResponseModel.suras
                ?.associateBy { it.id!!.toInt() } ?: emptyMap()
            val chapterMap = quranFileResponseModel.chapters
                ?.associateBy { it.id!!.toInt() } ?: emptyMap()

            // ✅ أهم تحسين - Map للـ ayas بره الـ loops
            val ayasMap: Map<Int, Map<Int, AyaModel>> = surahMap.mapValues { (_, surah) ->
                surah.ayas?.associateBy { it.id!!.toInt() } ?: emptyMap()
            }

            // ✅ ابني الـ list كاملة قبل تحديث الـ UI
            val newPages = buildList {
                for ((pageNumber, lines) in pagesGrouped) {
                    var lineSurahModel = EMPTY_SURAH
                    var lineChapterModel = EMPTY_CHAPTER

                    val lineModels = lines.sortedBy { it.line_number }.map { line ->
                        lineSurahModel = surahMap[line.surah_number] ?: EMPTY_SURAH
                        lineChapterModel = chapterMap[line.chapter_number] ?: EMPTY_CHAPTER

                        val wordList =
                            (line.first_word_id!!..line.last_word_id!!).mapNotNull { id ->
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
                                    word = word.word ?: 0
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
                            words = wordList
                        )
                    }

                    add(
                        QuranPageModel(
                            pageNumber = pageNumber,
                            surahModel = lineSurahModel,
                            lines = lineModels,
                            chapterModel = lineChapterModel
                        )
                    )
                }
            }


            // ✅ تحديث الـ UI مرة واحدة على Main thread
            withContext(Dispatchers.Main) {
                quranPageModels.clear()
                quranPageModels.addAll(newPages)
                isShowLoader = false
                isShowError = false
                isDataLoaded = true
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
    }

}
