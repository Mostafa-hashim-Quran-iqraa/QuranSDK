package com.blacksmith.quranApp.presentation.bookmarks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.blacksmith.quranApp.data.model.WordErrorModel
import com.blacksmith.quranlib.data.util.helper.toSP
import com.blacksmith.quranlib.presentation.theme.Black
import com.blacksmith.quranlib.presentation.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorWordBottomSheet(
    word: WordErrorModel,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Word text
            Text(
                text = word.wordText,
                fontSize = 26.toSP,
                fontFamily = com.blacksmith.quranlib.presentation.theme.amiri_quran,
                color = Color(0xFFE53935),
            )
            androidx.compose.material3.HorizontalDivider(color = Color(0xFFEEEEEE))
            // Details
            InfoRow(label = "السورة", value = "${word.surahName} (${word.surahId})")
            InfoRow(label = "الآية", value = "${word.ayah}")
            InfoRow(label = "الصفحة", value = "${word.page}")
            androidx.compose.material3.HorizontalDivider(color = Color(0xFFEEEEEE))
            Text(
                text = "تم تحديد خطأ حفظ",
                fontSize = 14.toSP,
                color = Color(0xFFE53935),
                fontFamily = com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular600,
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = value,
            fontSize = 15.toSP,
            color = Black,
            fontFamily = com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular400,
        )
        Text(
            text = label,
            fontSize = 14.toSP,
            color = Color.Gray,
            fontFamily = com.blacksmith.quranApp.presentation.base.theme.fontNeoSansArabicRegular400,
        )
    }
}