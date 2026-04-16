package com.blacksmith.quranApp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.blacksmith.quranApp.MyApplication
import com.blacksmith.quranApp.data.local.MyPreferences
import com.blacksmith.quranApp.data.providers.ResourceProviderInterface
import com.blacksmith.quranApp.data.remote.setup.NetworkCheck
import com.blacksmith.quranApp.data.resources.ResourceProviderImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplication(application: Application): MyApplication {
        return application as MyApplication
    }

    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        val prefName = context.packageName.plus("_preferences")
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                prefName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // مفتاح التشفير اتمسح من الـ Keystore (بعد مسح بيانات التطبيق)
            // امسح الـ prefs القديمة وابدأ من جديد
            context.deleteSharedPreferences(prefName)
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                prefName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    @Provides
    @Singleton
    fun getPreferencesHelper(
        context: Context,
        sharedPreferences: SharedPreferences
    ): MyPreferences {
        return MyPreferences(sharedPreferences, context)
    }

    @Provides
    @Singleton
    fun getNetworkCheck(
        context: Context,
    ): NetworkCheck {
        return NetworkCheck(context)
    }

    @Provides
    @Singleton
    fun provideResourceProvider(
        @ApplicationContext context: Context,
        myPreferences: MyPreferences
    ): ResourceProviderInterface {
        return ResourceProviderImp(context, myPreferences)
    }

    @Provides
    fun provideCoroutineContext(): CoroutineDispatcher = Dispatchers.IO
}