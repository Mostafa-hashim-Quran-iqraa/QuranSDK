package com.blacksmith.quranApp.presentation.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.blacksmith.quranApp.MyApplication
import com.blacksmith.quranApp.data.local.LocaleHelper
import com.blacksmith.quranApp.data.local.MyPreferences
import com.blacksmith.quranApp.data.local.PreferencesEntryPoint
import com.blacksmith.quranApp.presentation.splash.SplashActivity
import com.blacksmith.quranApp.presentation.base.theme.AppComposeTheme
import com.blacksmith.quranApp.presentation.base.theme.White
import com.blacksmith.quranApp.presentation.base.theme.colorPrimary
import com.blacksmith.quranApp.presentation.base.theme.krema
import com.blacksmith.quranApp.presentation.sub.BannerHost
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

@Suppress("UNUSED_EXPRESSION")
@AndroidEntryPoint
abstract class BaseActivity(
    val isWhiteActionBar: Boolean,
    val isFullScreen: Boolean,
    val noLimitScreen: Boolean
) : AppCompatActivity() {

    val baseViewModel: BaseViewModel by viewModels()

    @Inject
    lateinit var myApplication: MyApplication

    @Composable
    abstract fun DoCreate()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setScreenSettings()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        //put decor fit = false, to handle keyboard issues with TextField
        WindowCompat.setDecorFitsSystemWindows(window, false)
        updateLocale()
        baseViewModel.myPreferences.saveString(
            MyPreferences.DEVICE_ID_PREF_KEY,
            myApplication.getDeviceAndroidId()
        )

        setContent {
            // detect When orientation change
            when (LocalConfiguration.current.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    LocaleHelper.updateLocale(this, baseViewModel.myPreferences)
                }

                Configuration.ORIENTATION_PORTRAIT -> {
                    LocaleHelper.updateLocale(this, baseViewModel.myPreferences)
                }

                else -> {

                }
            }
            CompositionLocalProvider(
                if (BaseViewModel.isCheckLocale) LocaleHelper.getLayoutDirection(baseViewModel.myPreferences)
                else LocaleHelper.getLayoutDirection(baseViewModel.myPreferences)
            ) {
                AppComposeTheme(
                    baseViewModel, isWhiteActionBar = BaseViewModel.isWhiteActionBar
                ) {
                    ShowHideSystemUi(
                        isFullScreen = BaseViewModel.isFullScreen,
                        noLimitScreen = BaseViewModel.isNoLimitScreen
                    )
                    if (BaseViewModel.onBackClicked) {
                        baseViewModel.updateOnBackClicked(false)
                        onBackPressedDispatcher.onBackPressed()
                    }

                    if (BaseViewModel.isRestartApp) {
                        baseViewModel.updateRestartApp(false)
                        restartApp()
                    }
                    Scaffold(
                        modifier = Modifier
                            .let {
                                if (!BaseViewModel.isNoLimitScreen) {
                                    it
                                        .systemBarsPadding()
                                        .navigationBarsPadding()
                                } else {
                                    it
                                }
                            }
//                            .navigationBarsPadding()
                            //if apply it here, the hole app refresh when keyboard open,
                            // but the bottomBar not appear when keypad open
//                            .imePadding()
                            .fillMaxSize()
                            .background(krema),
                    ) {
                        it
                        Box(modifier = Modifier.fillMaxSize()) {
                            DoCreate()

                            if (!isInPictureInPictureMode)
                                BannerHost(
                                    bannerMessage = BaseViewModel.bannerMessage,
                                    onDismiss = { baseViewModel.showBannerMessage(null) }
                                )
                        }
                    }

                }
            }
        }
    }

    @Composable
    fun ShowHideSystemUi(isFullScreen: Boolean, noLimitScreen: Boolean) {
        val view = LocalView.current
        SideEffect {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            if (isFullScreen) {
                // Hide both the status bar and the navigation bar.
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    hide(WindowInsetsCompat.Type.systemBars())
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
            } else {
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    show(WindowInsetsCompat.Type.systemBars())
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }

                val window = (view.context as Activity).window
                window.statusBarColor =
                    if (isWhiteActionBar) White.toArgb() else colorPrimary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    isWhiteActionBar
                window.decorView.setOnApplyWindowInsetsListener { view, insets ->
                    view.setBackgroundColor(if (isWhiteActionBar) White.toArgb() else colorPrimary.toArgb())
                    view.setPadding(0, 0, 0, 0)
                    insets
                }
                if (noLimitScreen)
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    )
                else
                    window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            }
        }
    }

    fun updateLocale() {
        //Update the locale here before loading the layout to get localized strings for activity.
        LocaleHelper.updateLocale(this, baseViewModel.myPreferences)
        baseViewModel.checkLocale(!BaseViewModel.isCheckLocale)
    }

    fun restartApp() {
        Intent(this@BaseActivity, SplashActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val myPreferences = EntryPointAccessors.fromApplication(
            newBase.applicationContext,
            PreferencesEntryPoint::class.java
        ).myPreferences()
        val config = newBase.resources.configuration
        config.fontScale = 1.0f // to fix font size scale
        // to fix Display Size scale
//        val defaultDensityDpi = Resources.getSystem().displayMetrics.densityDpi
//        config.densityDpi = defaultDensityDpi
        val newContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(LocaleHelper.updateLocale(newContext, myPreferences))
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        super.applyOverrideConfiguration(
            LocaleHelper.applyOverrideConfiguration(
                baseContext, overrideConfiguration
            )
        )
    }


    fun setScreenSettings() {
        Handler(Looper.getMainLooper()).postDelayed({
            baseViewModel.fullScreen(isFullScreen)
            baseViewModel.noLimitScreen(noLimitScreen)
            baseViewModel.whiteActionBar(isWhiteActionBar)
        }, if (isFullScreen) 500 else 0)
    }

    override fun onResume() {
        super.onResume()
        setScreenSettings()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}