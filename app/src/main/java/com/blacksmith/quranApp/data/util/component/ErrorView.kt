package com.blacksmith.quranApp.data.util.component

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blacksmith.quranApp.R
import com.blacksmith.quranApp.presentation.base.theme.Black
import com.blacksmith.quranApp.presentation.base.theme.White
import com.blacksmith.quranApp.presentation.base.theme.colorPrimary
import com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular400
import com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular600

@Composable
@Preview
fun ErrorView(
    image: Int = R.drawable.ic_launcher_background,
    title: String = stringResource(R.string.error),
    titleColor: Color = Black,
    message: String = stringResource(id = R.string.app_name),
    messageColor: Color = Black,
    buttonBackgroundColor: Color = colorPrimary,
    buttonBorderColor: Color = colorPrimary,
    borderWidth: Int = 0,
    buttonTextColor: Color = White,
    isShowButtonAction: Boolean = true,
    withIcon: Boolean = false,
    icon: @Composable () -> Unit = {},
    buttonText: String = stringResource(R.string.try_again),
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
        Image(
            painter = painterResource(id = image),
            contentDescription = stringResource(id = R.string.app_name)
        )
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
                fontFamily = fontNeoSansArabicRegular600,
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
                fontFamily = fontNeoSansArabicRegular400,
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
