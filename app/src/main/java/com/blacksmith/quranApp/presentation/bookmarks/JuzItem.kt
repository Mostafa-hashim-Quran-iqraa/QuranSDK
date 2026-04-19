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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import com.blacksmith.quranApp.presentation.base.theme.colorPrimary
import com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular600
import com.blacksmith.quranlib.data.model.JuzIndexItem
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranlib.data.util.helper.toSP
import com.blacksmith.quranlib.presentation.theme.amiri_quran

@Composable
fun JuzItem(juz: JuzIndexItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.toDP, vertical = 12.toDP),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Right side: juz name + first aya preview
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                NumberCircleBadge(number = juz.juzId)
                Spacer(modifier = Modifier.width(6.toDP))
                Text(
                    text = juz.juzNameAr,
                    color = colorPrimary,
                    fontSize = 14.toSP,
                    fontFamily = fontNeoSansArabicRegular600,
                    fontWeight = FontWeight.Bold,
                )
            }
            // First aya preview (first surah's first aya text)
            val previewText = juz.surahs.firstOrNull()?.firstAyaText ?: ""
            if (previewText.isNotBlank()) {
                Text(
                    text = previewText,
                    color = Color(0xFF555555),
                    fontSize = 13.toSP,
                    fontFamily = amiri_quran,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.toDP),
                    style = androidx.compose.ui.text.TextStyle(
                        textDirection = TextDirection.Rtl,
                    ),
                )
            }
        }
        Spacer(modifier = Modifier.width(12.toDP))
        // Left side: page badge
        PageBadge(page = juz.surahs.firstOrNull()?.page ?: 1)
    }
}