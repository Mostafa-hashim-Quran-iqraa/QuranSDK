package com.blacksmith.quranApp.presentation.bookmarks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.blacksmith.quranApp.presentation.base.theme.colorPrimary
import com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular400
import com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular600
import com.blacksmith.quranlib.data.model.SurahListItem
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranlib.data.util.helper.toSP

@Composable
fun SurahItem(surah: SurahListItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.toDP, vertical = 12.toDP),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Right side: surah name + aya count
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NumberCircleBadge(number = surah.surahId)
            Spacer(modifier = Modifier.width(10.toDP))
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = surah.surahNameAr,
                    color = colorPrimary,
                    fontSize = 14.toSP,
                    fontFamily = fontNeoSansArabicRegular600,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${surah.ayaCount} آية",
                    color = Color.Gray,
                    fontSize = 12.toSP,
                    fontFamily = fontNeoSansArabicRegular400,
                )
            }
        }
        // Left side: page badge
        PageBadge(page = surah.page)
    }
}