package com.blacksmith.quranlib.data.util.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.blacksmith.quranlib.R
import com.blacksmith.quranlib.presentation.theme.colorPrimary

@Composable
@Preview
fun LoaderLottie(
    resId: Int = R.raw.loader_circle,
    color: Color = colorPrimary,
    width: Int = 30, height: Int = 30
) {
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                color.toArgb(),
                BlendModeCompat.SRC_ATOP
            ),
            keyPath = arrayOf(
                "**"
            )
        )
    )
    val lottieCompositionSpec =
        rememberLottieComposition(spec = LottieCompositionSpec.RawRes(resId))

    LottieAnimation(
        composition = lottieCompositionSpec.value,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .width(width.dp)
            .height(height.dp),
        dynamicProperties = dynamicProperties,
        speed = 1.0f,
        isPlaying = true,
    )
}