package com.blacksmith.quranApp.presentation.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import com.blacksmith.quranlib.data.model.AyaModel
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranlib.data.util.helper.toSP

@Composable
fun AyaSearchItem(aya: AyaModel, fontColor: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.toDP, vertical = 10.toDP),
    ) {
        Text(
            text = aya.text ?: "",
            color = fontColor,
            fontSize = 15.toSP,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(textDirection = TextDirection.Rtl),
        )
        Text(
            text = buildAyaInfo(aya),
            color = fontColor.copy(alpha = 0.6f),
            fontSize = 12.toSP,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(textDirection = TextDirection.Rtl),
            modifier = Modifier.padding(top = 3.toDP),
        )
    }
}

private fun buildAyaInfo(aya: AyaModel): String {
    val surahNumber = aya.surah?.id ?: 0
    val surahName = aya.surah?.name_ar ?: ""
    val ayaNumber = aya.aya ?: ""
    val page = aya.page ?: 0
    return "سورة $surahNumber $surahName • آية $ayaNumber • (ص $page)"
}