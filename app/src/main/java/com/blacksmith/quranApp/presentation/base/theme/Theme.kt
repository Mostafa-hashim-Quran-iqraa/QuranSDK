package com.blacksmith.quranApp.presentation.base.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.blacksmith.quranApp.data.local.MyPreferences
import com.blacksmith.quranApp.data.util.Constants
import com.blacksmith.quranApp.presentation.base.BaseViewModel

private val DarkColorScheme = darkColorScheme(
    primary = colorPrimary,
    secondary = colorAccent,
    tertiary = colorPrimary,
    background = WhiteSmoke,
    surface = White,
    onPrimary = colorPrimary,
    onSecondary = colorAccent,
    onTertiary = White,
    onBackground = White,
    onSurface = WhiteSmoke,
    surfaceTint = White,
    onSurfaceVariant = colorPrimary,

//    primaryContainer = colorPrimary,
//    onPrimaryContainer = colorPrimary,
//    inversePrimary = colorPrimary,
//    secondaryContainer = colorPrimary,
//    onSecondaryContainer = colorPrimary,
//    tertiaryContainer = colorPrimary,
//    onTertiaryContainer = colorPrimary,
//    surfaceVariant = colorPrimary,
//    inverseSurface = colorPrimary,
//    inverseOnSurface = colorPrimary,
//    error = colorPrimary,
//    onError = colorPrimary,
//    errorContainer = colorPrimary,
//    onErrorContainer = colorPrimary,
//    outline = colorPrimary,
//    outlineVariant = colorPrimary,
//    scrim = colorPrimary,
)

private val LightColorScheme = lightColorScheme(
    primary = colorPrimary,
    secondary = colorAccent,
    tertiary = colorPrimary,
    background = WhiteSmoke,
    surface = White,
    onPrimary = colorPrimary,
    onSecondary = colorAccent,
    onTertiary = White,
    onBackground = White,
    onSurface = WhiteSmoke,
    surfaceTint = White,
    onSurfaceVariant = colorPrimary,


//    primaryContainer = colorPrimary,
//    onPrimaryContainer = colorPrimary,
//    inversePrimary = colorPrimary,
//    secondaryContainer = colorPrimary,
//    onSecondaryContainer = colorPrimary,
//    tertiaryContainer = colorPrimary,
//    onTertiaryContainer = colorPrimary,
//    surfaceVariant = colorPrimary,
//    inverseSurface = colorPrimary,
//    inverseOnSurface = colorPrimary,
//    error = colorPrimary,
//    onError = colorPrimary,
//    errorContainer = colorPrimary,
//    onErrorContainer = colorPrimary,
//    outline = colorPrimary,
//    outlineVariant = colorPrimary,
//    scrim = colorPrimary,
)


@Composable
fun AppComposeTheme(
    viewModel: BaseViewModel,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    isWhiteActionBar: Boolean = true,
    isArabic: Boolean = viewModel.myPreferences.getString(MyPreferences.APP_LANGUAGE_PREF_KEY)
            == Constants.ARABIC_LOCALE_LANG,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor =
                if (isWhiteActionBar) krema.toArgb() else colorPrimary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                isWhiteActionBar
            window.decorView.setOnApplyWindowInsetsListener { view, insets ->
                view.setBackgroundColor(if (isWhiteActionBar) krema.toArgb() else colorPrimary.toArgb())
                view.setPadding(0, 0, 0, 0)
                insets
            }
        }
    }

    val typography = if (isArabic) {
        TypographyAr
    } else {
        TypographyEn
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}