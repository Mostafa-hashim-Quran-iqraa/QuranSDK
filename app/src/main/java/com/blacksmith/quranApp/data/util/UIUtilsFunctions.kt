package com.blacksmith.quranApp.data.util

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
            getSerializable(key, T::class.java)

        else -> @Suppress("DEPRECATION") getSerializable(key) as? T
    }

inline fun <reified T : Serializable> Intent.serializable(key: String): T? =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
            getSerializableExtra(key, T::class.java)

        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }

fun getSessionPermissions(): ArrayList<String> {
    val permissions = ArrayList<String>()
    permissions.add(Manifest.permission.RECORD_AUDIO)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    }
    return permissions
}

fun getPicturePermissions(): ArrayList<String> {
    val permissions = ArrayList<String>()
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    return permissions
}

@Composable
fun KeepScreenOn(activity: ComponentActivity) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = context.findActivity()?.window
        //set permission to show when screen locked
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            activity.setShowWhenLocked(true)
            activity.setTurnScreenOn(true)
            val keyguardManager =
                context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(activity, null)
        }
        window?.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
        onDispose {
            window?.clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
    }
}

fun Context.findActivity(): ComponentActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Picture in picture should be called in the context of an Activity")
}

fun getFormattedDateTime(timeStamp: String): String {
    return try {
        val dateTime = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ", Locale("en"))
            .parse(timeStamp)?.time

        //30/09/2020
        val dateFormat = SimpleDateFormat("d/MM/yyyy hh:mm a", Locale("en"))
        //8:55 pm
//        val timeFormat = SimpleDateFormat("hh:mm a", Locale("en"))
        val date = dateFormat.format(dateTime)
//        val time = timeFormat.format(dateTime)
        date
    } catch (e: Exception) {
        timeStamp
    }
}

fun requestOverlayPermission(context: Context) {
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
        data = "package:${context.packageName}".toUri()
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

fun getFormattedDateTime(timeMilli: Long): String {
    return try {
        val dateFormat = SimpleDateFormat("d/MM/yyyy hh:mm a", Locale("en"))
        val date = dateFormat.format(timeMilli)
        date
    } catch (e: Exception) {
        ""
    }
}


fun formatTimeTo12Hour(time: String, isArabic: Boolean): String {
    val inputFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val locale = if (isArabic) Locale.forLanguageTag("ar") else Locale.ENGLISH
    val outputFormatter = DateTimeFormatter.ofPattern("hh:mm a", locale)

    val localTime = LocalTime.parse(time, inputFormatter)
    return localTime.format(outputFormatter)
}

@Suppress("DEPRECATION")
fun getScreenSize(context: Context): Pair<Int, Int> {

    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // Android 11+
        val metrics = wm.currentWindowMetrics
        val insets = metrics.windowInsets.getInsetsIgnoringVisibility(
            WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout()
        )
        val bounds = metrics.bounds
        val width = bounds.width()
        val height = bounds.height() - insets.bottom
        width to height
    } else {
        // أقدم من Android 11
        val display = wm.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getRealMetrics(outMetrics)
        outMetrics.widthPixels to outMetrics.heightPixels
    }
}

