package com.blacksmith.quranlib.presentation.quranScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.blacksmith.quranlib.R
import com.blacksmith.quranlib.data.useCase.QuranSetupState
import com.blacksmith.quranlib.data.util.QuranConstants
import com.blacksmith.quranlib.data.util.helper.toSP
import com.blacksmith.quranlib.presentation.theme.colorPrimary

/**
 * Guards any composable content that requires Quran runtime assets.
 *
 * Shows a download/extraction progress screen when required files are missing,
 * then renders [content] once everything is ready.
 *
 * Usage — wrap any screen that needs Quran data:
 * ```kotlin
 * QuranDataGuard(versionNumber = QuranConstants.VERSION_KING_FAHD_1441) {
 *     MyQuranScreen()
 * }
 * ```
 *
 * For screens that only need DB + JSON (no page rendering), pass `needsFonts = false`:
 * ```kotlin
 * QuranDataGuard(versionNumber = QuranConstants.VERSION_KING_FAHD_1441, needsFonts = false) {
 *     BookmarksScreen(...)
 * }
 * ```
 *
 * @param versionNumber  The Quran print version being used (affects which font pack is downloaded).
 * @param needsFonts     Set to false for data-only screens (Juz index, Surah list, search).
 * @param setupViewModel Injected automatically via Hilt — no need to pass manually.
 * @param content        The composable content to show once setup is complete.
 */
@Composable
fun QuranDataGuard(
    versionNumber: Int = QuranConstants.VERSION_KING_FAHD_1441,
    needsFonts: Boolean = true,
    setupViewModel: QuranSetupViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(versionNumber, needsFonts) {
        setupViewModel.checkAndSetup(context, versionNumber, needsFonts)
    }

    when (val s = setupViewModel.state) {
        is QuranSetupState.Idle        -> SetupIdleLoader()
        is QuranSetupState.Downloading -> SetupProgressScreen(
            label       = s.label,
            progress    = s.progress / 100f,
            determinate = true,
        )
        is QuranSetupState.Extracting  -> SetupProgressScreen(
            label       = stringResource(R.string.preparing_quran_files),
            progress    = 0f,
            determinate = false,
        )
        is QuranSetupState.Done        -> content()
        is QuranSetupState.Error       -> SetupErrorScreen(
            message = s.message,
            onRetry = { setupViewModel.retry(context, versionNumber, needsFonts) },
        )
    }
}

@Composable
private fun SetupIdleLoader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = colorPrimary)
    }
}

@Composable
private fun SetupProgressScreen(label: String, progress: Float, determinate: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.holy_quran),
                fontSize = 20.toSP,
                color = colorPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = label,
                fontSize = 14.toSP,
                color = Color(0xFF555555),
                textAlign = TextAlign.Center,
            )
            if (determinate) {
                LinearProgressIndicator(
                    progress         = { progress },
                    modifier         = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color            = colorPrimary,
                    trackColor       = colorPrimary.copy(alpha = 0.15f),
                )
                Text(
                    text      = "${(progress * 100).toInt()}%",
                    fontSize  = 13.toSP,
                    color     = colorPrimary,
                )
            } else {
                LinearProgressIndicator(
                    modifier   = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color      = colorPrimary,
                    trackColor = colorPrimary.copy(alpha = 0.15f),
                )
            }
        }
    }
}

@Composable
private fun SetupErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text      = stringResource(R.string.downloading_failed),
                fontSize  = 20.toSP,
                color     = Color(0xFFE53935),
                textAlign = TextAlign.Center,
            )
            Text(
                text      = message,
                fontSize  = 13.toSP,
                color     = Color(0xFF555555),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorPrimary)
                    .clickable(onClick = onRetry)
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text     = stringResource(R.string.try_again),
                    color    = Color.White,
                    fontSize = 14.toSP,
                )
            }
        }
    }
}
