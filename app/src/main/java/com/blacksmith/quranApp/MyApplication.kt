package com.blacksmith.quranApp

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    @SuppressLint("HardwareIds")
    fun getDeviceAndroidId(): String {
        return Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}