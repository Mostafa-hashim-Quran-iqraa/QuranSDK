package com.blacksmith.quranApp.presentation.quran

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.blacksmith.quranApp.presentation.base.theme.gray_400
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranlib.data.util.helper.toSP
import com.blacksmith.quranlib.presentation.theme.gray_100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranIndexBottomSheet(
    viewModel: QuranViewModel,
    fontColor: Color,
    bgColor: Color,
    onSurahClick: (page: Int) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val surahListState = rememberLazyListState()

    // Scroll surah list to top whenever the user switches Juz
    LaunchedEffect(viewModel.selectedJuzId) {
        surahListState.scrollToItem(0)
    }

    val sheetScrollLock = remember {
        object : NestedScrollConnection {
            // Consume residual downward offset (list boundary → sheet dismiss axis)
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset = if (available.y > 0f) available else Offset.Zero

            // Consume ALL remaining fling velocity so the sheet can't use it
            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity,
            ): Velocity = available
        }
    }

    ModalBottomSheet(
        onDismissRequest = { viewModel.hideIndex() },
        sheetState = sheetState,
        containerColor = bgColor,
        dragHandle = {},
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .nestedScroll(sheetScrollLock)
        ) {
            // ── Header ────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.toDP, vertical = 6.toDP),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Decorative book icon
                Icon(
                    modifier = Modifier
                        .size(36.toDP)
                        .padding(6.toDP),
                    tint = fontColor,
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(6.toDP))
                // Title
                Text(
                    text = "فهرس المصحف",
                    color = fontColor,
                    fontSize = 16.toSP,
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(textDirection = TextDirection.Rtl),
                )
                Spacer(modifier = Modifier.weight(1f))
                // Close button (always on the physical left for consistent UX)
                Icon(
                    modifier = Modifier
                        .size(40.toDP)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = gray_400),
                            onClick = { viewModel.hideIndex() }
                        )
                        .padding(8.toDP),
                    tint = fontColor,
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close index",
                )
            }

            HorizontalDivider(color = fontColor.copy(alpha = 0.12f))

            // ── Body ──────────────────────────────────────────────────────────
            if (viewModel.juzIndex.isEmpty()) {
                // Loading indicator while juz data is being fetched
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = fontColor, strokeWidth = 2.5.dp)
                }
            } else {
                // Force LTR layout so juz column stays on the right regardless of locale
                Row(modifier = Modifier.fillMaxSize()) {
                    // ── Surah list (wide, left) ──────────────────────────
                    val selectedJuz =
                        viewModel.juzIndex.find { it.juzId == viewModel.selectedJuzId }
                    // ── Juz numbers column (narrow, right) ──────────────
                    LazyColumn(
                        modifier = Modifier
                            .width(110.toDP)
                            .fillMaxHeight()
                            .padding(horizontal = 4.toDP, vertical = 6.toDP),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(
                            items = viewModel.juzIndex,
                            key = { it.juzId },
                        ) { juz ->
                            JuzNumberItem(
                                juz = juz,
                                isSelected = juz.juzId == viewModel.selectedJuzId,
                                fontColor = fontColor,
                                onClick = { viewModel.selectedJuzId = juz.juzId },
                            )
                        }
                    }

                    LazyColumn(
                        state = surahListState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(gray_100),
                    )
                    {
                        items(
                            items = selectedJuz?.surahs ?: emptyList(),
                            key = { it.surahId },
                        ) { entry ->
                            SurahIndexItem(
                                entry = entry,
                                fontColor = fontColor,
                                onClick = { onSurahClick(entry.page) },
                            )
                            HorizontalDivider(
                                color = fontColor.copy(alpha = 0.06f),
                                thickness = 0.5.dp,
                            )
                        }
                    }
                }
            }
        }
    }
}