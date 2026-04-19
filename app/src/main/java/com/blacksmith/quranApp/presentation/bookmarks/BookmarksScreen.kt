package com.blacksmith.quranApp.presentation.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.blacksmith.quranApp.R
import com.blacksmith.quranApp.data.model.BookmarkModel
import com.blacksmith.quranApp.presentation.base.theme.White
import com.blacksmith.quranApp.presentation.base.theme.colorPrimary
import com.blacksmith.quranApp.presentation.base.theme.colorPrimaryDark
import com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular400
import com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular600
import com.blacksmith.quranApp.presentation.base.theme.gray_400
import com.blacksmith.quranApp.presentation.quran.QuranActivity
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranlib.data.util.helper.toSP
import com.blacksmith.quranlib.presentation.theme.amiri_quran

@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel,
    onItemClick: (BookmarkModel) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadBookmarks(context)
    }

    Surface(color = White, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.toDP, vertical = 8.toDP),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Icon(
                    modifier = Modifier
                        .width(30.toDP)
                        .height(30.toDP)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = gray_400),
                            onClick = { (context as BookmarksActivity).finish() }
                        )
                        .padding(5.toDP),
                    tint = colorPrimaryDark,
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "",
                )
                Text(
                    text = stringResource(R.string.bookmarks),
                    modifier = Modifier.padding(start = 10.toDP),
                    color = colorPrimaryDark,
                    fontSize = 16.toSP,
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            if (viewModel.bookmarks.isEmpty()) {
                // ── Empty state ───────────────────────────────────────────────
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "لا توجد مرجعيات محفوظة",
                        color = Color.Gray,
                        fontSize = 15.toSP,
                        fontFamily = fontNeoSansArabicRegular400,
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(viewModel.bookmarks) { index, bookmark ->
                        BookmarkItem(
                            bookmark = bookmark,
                            onClick = { onItemClick(bookmark) },
                            onDelete = {
                                viewModel.removeBookmark(context, bookmark.surahId, bookmark.ayah)
                            },
                        )
                        if (index < viewModel.bookmarks.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.toDP),
                                color = Color(0xFFEEEEEE),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkItem(
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
        Column(
            modifier = Modifier.weight(1f, false),
            horizontalAlignment = Alignment.End,
        )
        {
            // Surah name + aya number
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "سورة ${bookmark.surahName}",
                    color = colorPrimary,
                    fontSize = 13.toSP,
                    fontFamily = fontNeoSansArabicRegular600,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.size(6.toDP))
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
                Text(
                    text = "صفحة",
                    color = colorPrimary,
                    fontSize = 9.toSP,
                    fontFamily = fontNeoSansArabicRegular400,
                )
                Text(
                    text = "(${bookmark.page})",
                    color = colorPrimary,
                    fontSize = 14.toSP,
                    fontFamily = fontNeoSansArabicRegular600,
                    fontWeight = FontWeight.Bold,
                )

            }
            // Aya preview text
            Text(
                text = bookmark.ayaText,
                color = Color(0xFF333333),
                fontSize = 14.toSP,
                fontFamily = amiri_quran,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
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
