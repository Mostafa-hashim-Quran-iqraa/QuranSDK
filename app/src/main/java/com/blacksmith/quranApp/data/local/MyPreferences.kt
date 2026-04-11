package com.blacksmith.quranApp.data.local

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.content.edit
import androidx.core.content.pm.PackageInfoCompat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyPreferences @Inject constructor(
    private val prefs: SharedPreferences,
    val context: Context
) {

    init {
        if (prefs.getInt(
                APP_VERSION_PREF_KEY,
                -1
            ) == -1
        ) initializePreferences()
    }

    companion object {
        const val BaseUrl_PREF = "BaseUrl"
        const val APP_VERSION_PREF_KEY = "PREF_KEY_APP_VERSION"
        const val DEVICE_ID_PREF_KEY = "PREF_KEY_DEVICE_ID"
        const val APP_LANGUAGE_PREF_KEY = "PREF_KEY_APP_LANGUAGE"
        const val API_TOKEN_PREF_KEY = "PREF_KEY_ACCESS_TOKEN"
        const val FCM_TOKEN_PREF_KEY = "PREF_KEY_DEVICE_TOKEN"

    }

    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun initializePreferences() {
        val versionCode: Int
        try {
            val packageInfo = context.packageManager
                .getPackageInfo(context.packageName, 0)
            versionCode = PackageInfoCompat.getLongVersionCode(packageInfo).toInt()
            saveInt(APP_VERSION_PREF_KEY, versionCode)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    fun clearUserData() {
        val deviceToken = getString(FCM_TOKEN_PREF_KEY)
        val language = getString(APP_LANGUAGE_PREF_KEY)
        prefs.edit { clear() }
        saveString(FCM_TOKEN_PREF_KEY, deviceToken)
        saveString(APP_LANGUAGE_PREF_KEY, language)
    }

    fun deleteValue(key: String) {
        prefs.edit { remove(key) }
    }

    fun getString(key: String): String {
        return prefs.getString(key, "") ?: ""
    }

    fun saveString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    fun getInt(key: String): Int {
        return prefs.getInt(key, -1)
    }

    fun saveInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }

    fun getBoolean(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    fun saveBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

}