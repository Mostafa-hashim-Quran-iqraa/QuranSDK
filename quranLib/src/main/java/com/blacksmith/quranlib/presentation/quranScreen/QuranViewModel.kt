package com.blacksmith.quranlib.presentation.quranScreen

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksmith.quranlib.data.model.AyaModel
import com.blacksmith.quranlib.data.model.ChapterModel
import com.blacksmith.quranlib.data.model.LineModel
import com.blacksmith.quranlib.data.model.QuranPageModel
import com.blacksmith.quranlib.data.model.SurahModel
import com.blacksmith.quranlib.data.model.WordModel
import com.blacksmith.quranlib.domain.remote.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    init {
    }

    fun getData(context: Context) {
        viewModelScope.launch {
            isShowLoader = true
            isShowError = false
            isDataLoaded = false
            val quranFileResponseModel = async { quranRepository.getQuranData(context) }.await()
            val pages = async { quranRepository.getPages() }.await()
            val words = async { quranRepository.getWords() }.await()
            val wordsText = async { quranRepository.getWordsText() }.await()

            val wordsMap = words.associateBy { it.id }
            val wordsTextMap = wordsText.associateBy { it.id }

            val pagesGrouped = pages.groupBy { it.page_number }
            quranPageModels.clear()
            for ((pageNumber, lines) in pagesGrouped) {

                var lineSurahModel = SurahModel()
                var lineChapterModel = ChapterModel()

                val lineModels = lines.sortedBy { it.line_number }.map { line ->

                    val wordList = mutableListOf<WordModel>()
                    lineSurahModel =
                        quranFileResponseModel?.suras?.firstOrNull { it.id!!.toInt() == line.surah_number!! }
                            ?: SurahModel()
                    lineChapterModel =
                        quranFileResponseModel?.chapters?.firstOrNull { it.id!!.toInt() == line.chapter_number!! }
                            ?: ChapterModel()

                    for (id in line.first_word_id!!..line.last_word_id!!) {

                        val word = wordsMap[id] ?: continue
                        val wordTextItem = wordsTextMap[id]

                        val wordSurahModel =
                            quranFileResponseModel?.suras?.firstOrNull { it.id!!.toInt() == word.surah!! }
                                ?: SurahModel()
                        val ayaModel =
                            wordSurahModel.ayas?.firstOrNull { it.id!!.toInt() == word.ayah!! }
                                ?: AyaModel()
                        val wordChapterModel =
                            quranFileResponseModel?.chapters?.firstOrNull { it.id!!.toInt() == ayaModel.chapter_id!! }
                                ?: ChapterModel()

                        wordList.add(
                            WordModel(
                                id = word.id,
                                text = word.text ?: "",
                                wordText = wordTextItem?.text ?: "",
                                location = word.location,
                                surahId = word.surah ?: 0,
                                surahName = wordSurahModel.name_ar ?: "",
                                chapterId = wordChapterModel.id?.toIntOrNull() ?: 0,
                                chapterName = wordChapterModel.name_ar ?: "",
                                ayah = word.ayah ?: 0,
                                word = word.word ?: 0
                            )
                        )
                    }

                    LineModel(
                        lineNumber = line.line_number ?: 0,
                        isCentered = (line.is_centered == 1),
                        surahId = lineSurahModel.id!!.toInt(),
                        surahName = lineSurahModel.name_ar ?: "",
                        surahLigature = lineSurahModel.ligature ?: "",
                        chapterId = lineChapterModel.id?.toIntOrNull() ?: 0,
                        chapterName = lineChapterModel.name_ar ?: "",
                        line_type = line.line_type,
                        words = wordList
                    )
                }

                //get surah for this page
                quranPageModels.add(
                    QuranPageModel(
                        pageNumber = pageNumber ?: 0,
                        surahModel = lineSurahModel,
                        lines = lineModels,
                        chapterModel = lineChapterModel
                    )
                )
            }
            isShowLoader = false
            isShowError = false
            isDataLoaded = true
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

}
