package com.blacksmith.quranApp.presentation.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.blacksmith.quranApp.data.local.MyPreferences
import com.blacksmith.quranApp.data.providers.ResourceProviderInterface
import com.blacksmith.quranApp.presentation.sub.BannerMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
) : ViewModel() {

    @Inject
    lateinit var resourceProvider: ResourceProviderInterface

    @Inject
    lateinit var myPreferences: MyPreferences

    fun fullScreen(value: Boolean) {
        isFullScreen = value
    }

    fun noLimitScreen(value: Boolean) {
        isNoLimitScreen = value
    }

    fun updateOnBackClicked(value: Boolean) {
        onBackClicked = value
    }

    fun whiteActionBar(value: Boolean) {
        isWhiteActionBar = value
    }

    fun showAppBar(value: Boolean) {
        isShowAppBar = value
    }

    fun checkLocale(value: Boolean) {
        isCheckLocale = value
    }

    fun isArabicLocale(): Boolean {
        return myPreferences.getString(MyPreferences.APP_LANGUAGE_PREF_KEY).compareTo("ar") == 0
    }


    override fun onCleared() {
        super.onCleared()
    }

    companion object {
        var isWhiteActionBar by mutableStateOf(false)
            private set
        var isShowAppBar by mutableStateOf(false)
            private set
        var onBackClicked by mutableStateOf(false)
            private set
        var isFullScreen by mutableStateOf(false)
            private set

        var isNoLimitScreen by mutableStateOf(false)
            private set
        var isCheckLocale by mutableStateOf(false)
            private set
        var errorTitle = ""
        var errorMessage = ""
        var isShowDialogLoader by mutableStateOf(false)
            private set
        var isShowMessageDialog by mutableStateOf(false)
            private set

        var isShowLoginDialog by mutableStateOf(false)
            private set
        var bannerMessage by mutableStateOf<BannerMessage?>(null)
            private set
        var isRestartApp by mutableStateOf(false)
            private set
    }

    fun updateRestartApp(value: Boolean) {
        isRestartApp = value
    }

    fun showBannerMessage(value: BannerMessage?) {
        bannerMessage = value
    }

    fun showLoadingDialog(isShow: Boolean) {
        isShowDialogLoader = isShow
    }

    fun updateShowMessageDialog(value: Boolean) {
        isShowMessageDialog = value
    }

    fun showMessageDialog(isShow: Boolean, title: String, message: String) {
        errorTitle = title
        errorMessage = message
        isShowMessageDialog = isShow
    }
}