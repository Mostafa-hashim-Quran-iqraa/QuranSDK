package com.blacksmith.quranlib.data.util.helper

import android.app.Activity
import android.content.Context
import android.text.Html
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.security.SecureRandom
import java.util.Locale
import java.util.regex.Pattern

fun getRandomNumber(): Int {
    val r = SecureRandom()
    val low = 10000
    val high = 1000000000
    return (System.currentTimeMillis() % Integer.MAX_VALUE).toInt() + (r.nextInt(high - low) + low)
}

fun hideKeyboard(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = (context as Activity).currentFocus
    if (view != null) {
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

// this fun use for check if value is number
fun isNumber(value: String): Boolean {
    return value.isEmpty() || Regex("^\\d+\$").matches(value)
}

fun isNameValid(name: String): Boolean {
    // arabic and english letters only
    val regex = "^[\\p{InArabic}a-zA-Z\\s]+$".toRegex()
    return name.matches(regex)
}

fun isEmailValid(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isPasswordValid(password: String): Boolean {
//    val UserName_PATTERN = "^(?=.{6,40}\$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])\$"
    //mostafa_hashim93, Mostafa-hashim50
    val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"
    return Pattern.compile(passwordPattern).matcher(password).matches()
}

fun isUserNameValid(username: String) = CoroutineScope(Dispatchers.Default).async {
//    val UserName_PATTERN = "^(?=.{6,40}\$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])\$"
    //mostafa_hashim93, Mostafa-hashim50
    val userNamePATTERN = "^[A-Za-z0-9_]+$"
    return@async Pattern.compile(userNamePATTERN).matcher(username).matches()
}

fun convertHtmlToString(htmlText: String?): String {
    if (htmlText == null) return ""
    return Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY).toString()
}

val LazyListState.isLastItemVisible: Boolean
    get() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
val LazyGridState.isLastItemVisible: Boolean
    get() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

val LazyListState.isFirstItemVisible: Boolean
    get() = firstVisibleItemIndex == 0

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

fun secondsToMinutes(seconds: Int): String {
    return String.format(
        Locale("en"),
        "%02d:%02d",
        (seconds / 3600 * 60 + ((seconds % 3600) / 60)),
        (seconds % 60)
    )
}