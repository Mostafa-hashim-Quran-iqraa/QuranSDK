package com.blacksmith.quranApp.presentation.quran

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blacksmith.quranApp.presentation.base.theme.gray_400
import com.blacksmith.quranlib.data.model.JuzIndexItem
import com.blacksmith.quranlib.data.util.helper.toSP

// =============================================================================
// JuzNumberItem  —  single numbered box in the juz column
// =============================================================================
@Composable
fun JuzNumberItem(
    juz: JuzIndexItem,
    isSelected: Boolean,
    fontColor: Color,
    onClick: () -> Unit,
) {
    val selectedColor = Color(0xFF1565C0)
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 0.dp)
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) selectedColor else fontColor.copy(alpha = 0.18f),
                shape = RoundedCornerShape(8.dp),
            )
            .background(
                color = if (isSelected) selectedColor.copy(alpha = 0.08f) else Color.Transparent,
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = gray_400),
                onClick = onClick,
            ).padding(horizontal = 4.dp, vertical = 0.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = juz.juzNameAr,
            color = if (isSelected) selectedColor else fontColor,
            fontSize = 13.toSP,
            textAlign = TextAlign.Center,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}