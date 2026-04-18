package com.blacksmith.quranlib.data.useCase

import com.blacksmith.quranlib.data.model.PageEntity
import com.blacksmith.quranlib.data.model.WordEntity
import com.blacksmith.quranlib.domain.remote.QuranRepository
import javax.inject.Inject

// جوّا الـ SDK
class GetWordsUseCase @Inject constructor(
    private val repository: QuranRepository
) {
    suspend operator fun invoke(): List<WordEntity> = repository.getWords()
}

class GetPagesUseCase @Inject constructor(
    private val repository: QuranRepository
) {
    suspend operator fun invoke(version: Int): List<PageEntity> = repository.getPages(version)
}