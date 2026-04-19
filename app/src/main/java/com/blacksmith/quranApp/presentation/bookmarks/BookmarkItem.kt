package com.blacksmith.quranApp.presentation.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.blacksmith.quranApp.data.model.BookmarkModel
import com.blacksmith.quranApp.presentation.base.theme.colorPrimary
import com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular400
import com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular600
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranlib.data.util.helper.toSP
import com.blacksmith.quranlib.presentation.theme.amiri_quran

@Composable
fun BookmarkItem(
    bookmark: BookmarkModel,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.toDP, vertical = 12.toDP),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        PageBadge(page = bookmark.page)
        Spacer(modifier = Modifier.size(4.toDP))
        Column(
            modifier = Modifier.weight(1f, fill = false),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(
                    text = "آية ${bookmark.ayah}",
                    color = colorPrimary,
                    fontSize = 12.toSP,
                    fontFamily = fontNeoSansArabicRegular400,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(colorPrimary.copy(alpha = 0.08f))
                        .padding(horizontal = 6.toDP, vertical = 2.toDP),
                )
                Spacer(modifier = Modifier.size(6.toDP))
                Text(
                    text = "سورة ${bookmark.surahName}",
                    color = colorPrimary,
                    fontSize = 13.toSP,
                    fontFamily = fontNeoSansArabicRegular600,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = bookmark.ayaText,
                color = Color(0xFF333333),
                fontSize = 14.toSP,
                fontFamily = amiri_quran,
                maxLines = 2,
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

        Box(
            modifier = Modifier
                .size(36.toDP)
                .clip(CircleShape)
                .border(1.dp, Color(0xFFEEEEEE), CircleShape)
                .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "حذف",
                tint = Color(0xFFE57373),
                modifier = Modifier.size(18.toDP),
            )
        }
    }
}