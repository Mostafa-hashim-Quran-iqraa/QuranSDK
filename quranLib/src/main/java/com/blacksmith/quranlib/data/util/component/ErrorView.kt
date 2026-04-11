package com.blacksmith.quranlib.data.util.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blacksmith.quranlib.presentation.theme.Black
import com.blacksmith.quranlib.presentation.theme.White
import com.blacksmith.quranlib.presentation.theme.colorPrimary

@Composable
@Preview
fun ErrorView(
    title: String = "",
    titleColor: Color = Black,
    message: String = "",
    messageColor: Color = Black,
    buttonBackgroundColor: Color = colorPrimary,
    buttonBorderColor: Color = colorPrimary,
    borderWidth: Int = 0,
    buttonTextColor: Color = White,
    isShowButtonAction: Boolean = true,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth(),
            color = titleColor,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight(600),
            )
        )
        Text(
            text = message,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 5.dp)
                .fillMaxWidth(),
            color = messageColor,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight(400),
            )
        )

        Spacer(modifier = Modifier.padding(top = 40.dp))

        if (isShowButtonAction) {
//            RectangleButton(
//                onClick = onClick,
//                height = 50,
//                roundCorner = 12,
//                text = buttonText,
//                textSize = 16,
//                textColor = buttonTextColor,
//                textFontFamily = fontNeoSansArabicRegular400,
//                backgroundColor = buttonBackgroundColor,
//                pressedBackgroundColor = buttonBackgroundColor.copy(alpha = 0.90f),
//                borderWidth = borderWidth,
//                colorBorder = buttonBorderColor,
//                elevation = 0,
//                withIcon = withIcon,
//                paddingIcon = 5,
//                icon = icon
//            )
        }
    }

}
