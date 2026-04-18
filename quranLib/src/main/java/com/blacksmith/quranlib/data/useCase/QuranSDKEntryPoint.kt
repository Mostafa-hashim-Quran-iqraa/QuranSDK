package com.blacksmith.quranlib.data.useCase

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface QuranSDKEntryPoint {
    fun getWordsUseCase(): GetWordsUseCase
    fun getPagesUseCase(): GetPagesUseCase
}