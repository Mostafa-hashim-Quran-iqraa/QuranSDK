package com.blacksmith.quranlib.di

import android.content.Context
import com.blacksmith.quranlib.data.local.database.DatabaseCopier
import com.blacksmith.quranlib.data.local.database.DatabaseProvider
import com.blacksmith.quranlib.data.respositoryImp.QuranRepositoryImp
import com.blacksmith.quranlib.domain.remote.QuranRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Repositories {

    @Singleton
    @Provides
    fun providesQuranRepository(
        @ApplicationContext context: Context,
    ): QuranRepository {
        DatabaseCopier.copyDatabase(context, "qpc_v4_tajweed_15_lines.db")
        val db = DatabaseProvider.openDatabase(context, "qpc_v4_tajweed_15_lines.db")
        return QuranRepositoryImp(db)
    }
}