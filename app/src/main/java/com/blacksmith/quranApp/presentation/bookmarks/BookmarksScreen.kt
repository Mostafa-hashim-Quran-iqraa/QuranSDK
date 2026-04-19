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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.blacksmith.quranlib.data.model.JuzIndexItem
import com.blacksmith.quranlib.data.model.SurahListItem
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranlib.data.util.helper.toSP
import com.blacksmith.quranlib.presentation.theme.amiri_quran

// =============================================================================
// Public entry point
// =============================================================================

/**
 * @param onNavigateTo  (page, ayaNumber, surahId) — ayaNumber / surahId == -1 when
 *                      navigating to a page without aya highlight (juz / surah tap).
 */
@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel,
    onNavigateTo: (page: Int, ayaNumber: Int, surahId: Int) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadBookmarks(context)
        viewModel.loadJuzList(context)
        viewModel.loadSurahList(context)
    }

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("الأجزاء", "السور", "المرجعيات")

    Column(modifier = Modifier.fillMaxSize()) {

        // ── AppBar ────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.toDP, vertical = 8.toDP),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .width(30.toDP)
                    .height(30.toDP)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = gray_400),
                        onClick = { (context as BookmarksActivity).finish() },
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
        }

        // ── Tab row ───────────────────────────────────────────────────────────
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = White,
            contentColor = colorPrimary,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = colorPrimary,
                )
            },
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontFamily = fontNeoSansArabicRegular600,
                            fontSize = 14.toSP,
                            color = if (selectedTab == index) colorPrimary else Color.Gray,
                        )
                    },
                )
            }
        }

        // ── Tab content ───────────────────────────────────────────────────────
        when (selectedTab) {
            0 -> JuzTab(
                juzList = viewModel.juzList,
                isLoading = viewModel.isLoadingJuz,
                onItemClick = { juz -> onNavigateTo(juz.surahs.firstOrNull()?.page ?: 1, -1, -1) },
            )

            1 -> SurahTab(
                surahList = viewModel.surahList,
                isLoading = viewModel.isLoadingSurahs,
                onItemClick = { surah -> onNavigateTo(surah.page, -1, -1) },
            )

            2 -> BookmarksTab(
                bookmarks = viewModel.bookmarks,
                onItemClick = { bm -> onNavigateTo(bm.page, bm.ayah, bm.surahId) },
                onDelete = { bm -> viewModel.removeBookmark(context, bm.surahId, bm.ayah) },
            )
        }
    }
}

// =============================================================================
// Tab: الأجزاء
// =============================================================================
@Composable
private fun JuzTab(
    juzList: List<JuzIndexItem>,
    isLoading: Boolean,
    onItemClick: (JuzIndexItem) -> Unit,
) {
    when {
        isLoading -> CenteredLoader()
        juzList.isEmpty() -> EmptyState("لا توجد بيانات")
        else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(juzList) { index, juz ->
                JuzItem(juz = juz, onClick = { onItemClick(juz) })
                if (index < juzList.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.toDP),
                        color = Color(0xFFEEEEEE),
                    )
                }
            }
        }
    }
}


// =============================================================================
// Tab: السور
// =============================================================================
@Composable
private fun SurahTab(
    surahList: List<SurahListItem>,
    isLoading: Boolean,
    onItemClick: (SurahListItem) -> Unit,
) {
    when {
        isLoading -> CenteredLoader()
        surahList.isEmpty() -> EmptyState("لا توجد بيانات")
        else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(surahList) { index, surah ->
                SurahItem(surah = surah, onClick = { onItemClick(surah) })
                if (index < surahList.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.toDP),
                        color = Color(0xFFEEEEEE),
                    )
                }
            }
        }
    }
}


// =============================================================================
// Tab: المرجعيات
// =============================================================================
@Composable
private fun BookmarksTab(
    bookmarks: List<BookmarkModel>,
    onItemClick: (BookmarkModel) -> Unit,
    onDelete: (BookmarkModel) -> Unit,
) {
    when {
        bookmarks.isEmpty() -> EmptyState("لا توجد مرجعيات محفوظة")
        else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(bookmarks) { index, bookmark ->
                BookmarkItem(
                    bookmark = bookmark,
                    onClick = { onItemClick(bookmark) },
                    onDelete = { onDelete(bookmark) },
                )
                if (index < bookmarks.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.toDP),
                        color = Color(0xFFEEEEEE),
                    )
                }
            }
        }
    }
}


// =============================================================================
// Shared helpers
// =============================================================================

@Composable
fun PageBadge(page: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "صفحة",
            color = Color.Gray,
            fontSize = 9.toSP,
            fontFamily = fontNeoSansArabicRegular400,
        )

        Text(
            text = "$page",
            color = colorPrimary,
            fontSize = 14.toSP,
            fontFamily = fontNeoSansArabicRegular600,
            fontWeight = FontWeight.Bold,
        )

    }
}

@Composable
fun NumberCircleBadge(number: Int) {
    Box(
        modifier = Modifier
            .size(28.toDP)
            .clip(CircleShape)
            .background(colorPrimary.copy(alpha = 0.10f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$number",
            color = colorPrimary,
            fontSize = 11.toSP,
            fontFamily = fontNeoSansArabicRegular600,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CenteredLoader() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = colorPrimary, strokeWidth = 2.dp)
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            color = Color.Gray,
            fontSize = 15.toSP,
            fontFamily = fontNeoSansArabicRegular400,
        )
    }
}
