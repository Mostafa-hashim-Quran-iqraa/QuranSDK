package com.blacksmith.quranlib.helper

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

val Int.toDP: Dp
    @Composable
    get() = this.dimensionDPGet()

@Composable
private fun Int.dimensionDPGet(): Dp {
    val id = when (this) {
        in 0..700 -> "padding_${this}"
        in (-100..-1) -> "padding_minus_${this}"
        else -> return this.dp
    }

    val resourceField = getFieldId(id)
    return if (resourceField != 0) dimensionResource(id = resourceField) else this.dp
}

val Int.toSP: TextUnit
    @Composable
    get() = this.dimensionSPGet()


@Composable
private fun Int.dimensionSPGet(): TextUnit {
    val id = when (this) {
        in 8..70 -> "font_size_${this}"
        else -> return this.sp
    }

    val resourceField = getFieldId(id)
    return if (resourceField != 0) dimensionResource(id = resourceField).value.sp else this.sp

//    val fontSize =
//        if (resourceField != 0) dimensionResource(id = resourceField).value.sp else this.sp
//    val density = LocalDensity.current
//    val fontSizeFixed = fontSize.value / density.fontScale
//    return fontSizeFixed.sp
}

@SuppressLint("DiscouragedApi")
@Composable
private fun getFieldId(id: String): Int {
    val context = LocalContext.current
    return context.resources.getIdentifier(id, "dimen", context.packageName)
}
