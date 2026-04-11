package com.blacksmith.quranApp.presentation.sub

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.blacksmith.quranApp.presentation.base.theme.Black
import com.blacksmith.quranApp.presentation.base.theme.Gray50
import com.blacksmith.quranApp.presentation.base.theme.Success700
import com.blacksmith.quranApp.presentation.base.theme.Warning50
import com.blacksmith.quranApp.presentation.base.theme.red
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranlib.data.util.helper.toSP
import kotlinx.coroutines.delay

@Composable
fun AutoDismissBanner(
    message: String,
    isSuccess: Boolean,
    durationMillis: Long = 3000,
    onDismiss: () -> Unit
) {
    LaunchedEffect(message) {
        delay(durationMillis)
        onDismiss()
    }

    val backgroundColor = if (isSuccess) Gray50 else Warning50
    val borderColor = if (isSuccess) Success700 else red
    val textColor = Black
    val icon = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.toDP, vertical = 8.toDP)
            .clip(RoundedCornerShape(12.toDP))
            .border(width = 1.toDP, color = borderColor, shape = RoundedCornerShape(12.toDP))
            .background(backgroundColor, )
            .padding(12.toDP)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = borderColor)
            Spacer(modifier = Modifier.width(8.toDP))
            Text(
                text = message,
                color = textColor,
                fontSize = 16.toSP,
                fontWeight = FontWeight(400),
            )
        }
    }
}

@Composable
fun BannerHost(
    bannerMessage: BannerMessage?,
    onDismiss: () -> Unit
) {
    bannerMessage?.let { msg ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 35.toDP) // تحت الـ status bar
                .wrapContentHeight()
        ) {
            AutoDismissBanner(
                message = msg.text,
                isSuccess = msg.isSuccess,
                onDismiss = onDismiss
            )
        }
    }
}

data class BannerMessage(
    val text: String,
    val isSuccess: Boolean
)