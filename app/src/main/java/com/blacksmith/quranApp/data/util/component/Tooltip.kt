package com.blacksmith.quranApp.data.util.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.blacksmith.quranApp.presentation.base.theme.Gray700
import com.blacksmith.quranApp.presentation.base.theme.White
import com.blacksmith.quranApp.presentation.base.theme.gray_400
import com.blacksmith.quranApp.presentation.base.theme.transparent
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranlib.data.util.helper.toSP

@Composable
fun Tooltip(
    buttonPosition: Offset = Offset.Zero,
    buttonSize: IntSize = IntSize.Zero,
    showTooltip: Boolean,
    text: String,
    textSize: TextUnit = 14.toSP,
    fontFamily: FontFamily,
    textColor: Color = White,
    buttonTextSize: TextUnit = 14.toSP,
    buttonFontFamily: FontFamily,
    buttonTextColor: Color = White,
    buttonText: String,
    backgroundColor: Color = Gray700,
    onButtonClick: () -> Unit
) {
    if (showTooltip)
        Popup(
            popupPositionProvider = object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize
                ): IntOffset {
                    val x =
                        buttonPosition.x.toInt() + (buttonSize.width / 2) - (popupContentSize.width / 2)
                    val y =
                        buttonPosition.y.toInt() - popupContentSize.height - 10
                    return IntOffset(x, y)
                }
            },
//        onDismissRequest = onTooltipDismiss,
            onDismissRequest = { },
            properties = PopupProperties(focusable = false),
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 250.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            )
            {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = transparent,
                    shadowElevation = 4.dp,
                )
                {
                    Column(
                        modifier = Modifier.padding(0.toDP)
                            .background(
                                color = backgroundColor,
                                shape = RoundedCornerShape(8.toDP)
                            )
                            .padding(12.toDP),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    )
                    {
                        Text(
                            text = text,
                            color = textColor,
                            fontSize = textSize,
                            fontFamily = fontFamily
                        )
                        //button
                        Text(
                            modifier = Modifier
                                .padding(top = 5.toDP)
                                .clip(RoundedCornerShape(8.toDP))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(color = gray_400),
                                    onClick = {
                                        onButtonClick()
                                    }
                                )
                                .padding(5.toDP),
                            text = buttonText,
                            color = buttonTextColor,
                            fontSize = buttonTextSize,
                            fontFamily = buttonFontFamily
                        )
                    }
                }
                val density = LocalDensity.current
                // arrow below
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    val tooltipWidthPx = with(density) { maxWidth.toPx() }
                    val relativeArrowX =
                        ((buttonPosition.x + (buttonSize.width / 2)) - (buttonPosition.x + buttonSize.width / 2 - tooltipWidthPx / 2))
                            .coerceIn(15f, tooltipWidthPx - 15f)
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    ) {
                        val path = Path().apply {
                            moveTo(relativeArrowX - 15f, 0f)
                            lineTo(relativeArrowX, size.height)
                            lineTo(relativeArrowX + 20f, 0f)
                            close()
                        }
                        drawPath(path, color = backgroundColor)
                    }
                }
            }
        }
}
