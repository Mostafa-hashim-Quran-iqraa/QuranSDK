package com.blacksmith.quranApp.presentation.base.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.blacksmith.quranApp.R


// Set of Material typography styles to start with
val fontNeoSansArabicLight300 = FontFamily(Font(R.font.noto_sans_arabic_arabic_300))//300
val fontNeoSansArabicRegular400 = FontFamily(Font(R.font.noto_sans_arabic_arabic_400))//400
val fontNeoSansArabicMedium500 = FontFamily(Font(R.font.noto_sans_arabic_arabic_500))//500
val fontNeoSansArabicRegular600 = FontFamily(Font(R.font.noto_sans_arabic_arabic_600))//400
val fontNeoSansArabicBold700 = FontFamily(Font(R.font.noto_sans_arabic_arabic_700))//700
val fontNeoSansArabicBold800 = FontFamily(Font(R.font.noto_sans_arabic_arabic_800))//700
val fontNeoSansArabicBold900 = FontFamily(Font(R.font.noto_sans_arabic_arabic_900))//900
val TypographyEn = Typography(
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontFamily = fontNeoSansArabicMedium500,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    /* Other default text styles to override*/
    titleLarge = TextStyle(
        fontFamily = fontNeoSansArabicBold700,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = fontNeoSansArabicRegular400,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

val TypographyAr = Typography(
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontFamily = fontNeoSansArabicMedium500,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    /* Other default text styles to override*/
    titleLarge = TextStyle(
        fontFamily = fontNeoSansArabicBold700,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = fontNeoSansArabicRegular400,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)