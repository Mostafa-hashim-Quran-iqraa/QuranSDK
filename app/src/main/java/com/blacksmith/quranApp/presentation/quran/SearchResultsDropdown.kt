package com.blacksmith.quranApp.presentation.quran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.blacksmith.quranlib.data.model.AyaModel
import com.blacksmith.quranlib.data.util.helper.toDP

@Composable
fun SearchResultsDropdown(
    results: List<AyaModel>,
    isLoading: Boolean,
    fontColor: Color,
    bgColor: Color,
    onItemClick: (AyaModel) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 320.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(
                color = bgColor.copy(alpha = 0.97f),
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            )
    ) {
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.toDP),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = fontColor,
                        strokeWidth = 2.dp,
                    )
                }
            }
        }
        items(items = results, key = { it.id ?: it.hashCode().toString() }) { aya ->
            AyaSearchItem(aya = aya, fontColor = fontColor, onClick = { onItemClick(aya) })
            HorizontalDivider(color = fontColor.copy(alpha = 0.08f), thickness = 0.5.dp)
        }
    }
}