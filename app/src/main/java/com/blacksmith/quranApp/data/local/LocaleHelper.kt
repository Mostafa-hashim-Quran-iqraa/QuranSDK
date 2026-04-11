package com.blacksmith.quranApp.data.local

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.blacksmith.quranApp.data.util.Constants
import java.util.*

object LocaleHelper {

    private fun getLanguageCode(myPreferences: MyPreferences): String {
        val languageCode = myPreferences.getString(MyPreferences.APP_LANGUAGE_PREF_KEY)
        return if (languageCode.compareTo(Constants.ENGLISH_LOCALE_LANG) == 0
        )
            Constants.ENGLISH_LOCALE_LANG
        else Constants.ARABIC_LOCALE_LANG
    }

    fun getLayoutDirection(myPreferences: MyPreferences): ProvidedValue<LayoutDirection> {
        return if (getLanguageCode(myPreferences) == Constants.ARABIC_LOCALE_LANG
        )
            LocalLayoutDirection provides LayoutDirection.Rtl
        else
            LocalLayoutDirection provides LayoutDirection.Ltr
    }

    fun updateLocale(base: Context, myPreferences: MyPreferences): Context {
        val languageCode = getLanguageCode(myPreferences)
        myPreferences.saveString(MyPreferences.APP_LANGUAGE_PREF_KEY, languageCode)
        languageCode.let {
            return if (it.isNotEmpty()) {
                updateResources(base, it)
            } else {
                base
            }
        }
    }

    fun applyOverrideConfiguration(
        base: Context,
        overrideConfiguration: Configuration?
    ): Configuration? {
//        if (overrideConfiguration != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//        if (overrideConfiguration != null && Build.VERSION.SDK_INT in 21..25) {
        if (overrideConfiguration != null) {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(base.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        return overrideConfiguration
    }

    private fun updateResources(base: Context, language: String): Context {
        var newBase = base
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = base.resources.configuration
        newBase = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)

            newBase.resources.updateConfiguration(configuration, base.resources.displayMetrics)
            newBase.createConfigurationContext(configuration)
        } else {
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)
            newBase.resources.updateConfiguration(configuration, base.resources.displayMetrics)
            newBase.createConfigurationContext(configuration)
        }
        return newBase
    }

}