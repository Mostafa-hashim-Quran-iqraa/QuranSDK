package com.blacksmith.quranApp.presentation.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import com.blacksmith.quranApp.presentation.base.theme.gray_400
import com.blacksmith.quranlib.data.model.SurahIndexEntry
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranlib.data.util.helper.toSP

// =============================================================================
// SurahIndexItem  —  single row in the surah list
// =============================================================================
@Composable
fun SurahIndexItem(
    entry: SurahIndexEntry,
    fontColor: Color,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = gray_400),
                onClick = onClick,
            )
            .padding(horizontal = 16.toDP, vertical = 10.toDP),
    ) {
        // Surah name
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
            ) {
            Text(
                text = "سورة ${entry.surahNameAr}",
                color = fontColor,
                fontSize = 15.toSP,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.wrapContentSize(),
            )
            Text(
                text = "صفحة (${entry.page})",
                color = fontColor.copy(alpha = 0.85f),
                fontSize = 14.toSP,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 10.toDP)
                    .wrapContentSize(),
            )
        }

        // First aya preview
        Text(
            text = entry.firstAyaText,
            color = fontColor.copy(alpha = 0.55f),
            fontSize = 12.toSP,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 0.toDP)
                .fillMaxWidth(),
        )
        // First aya preview

    }
}