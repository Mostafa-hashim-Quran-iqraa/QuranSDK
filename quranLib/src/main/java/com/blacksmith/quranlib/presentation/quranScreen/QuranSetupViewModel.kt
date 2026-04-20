package com.blacksmith.quranlib.presentation.quranScreen

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksmith.quranlib.R
import com.blacksmith.quranlib.data.useCase.QuranSetupState
import com.blacksmith.quranlib.data.util.QuranDownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranSetupViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf<QuranSetupState>(QuranSetupState.Idle)
        private set

    /** Prevents duplicate setup launches. Reset on [retry]. */
    private var setupStarted = false

    /**
     * Checks whether required files are present and downloads them if not.
     *
     * @param versionNumber  Print version (see [QuranConstants])
     * @param needsFonts     false when only DB + JSON are needed (e.g., search / index screens)
     */
    fun checkAndSetup(context: Context, versionNumber: Int, needsFonts: Boolean = true) {
        if (setupStarted) return
        val alreadyReady = if (needsFonts)
            QuranDownloadManager.isAllReady(context, versionNumber)
        else
            QuranDownloadManager.isDataReady(context)
        if (alreadyReady) {
            state = QuranSetupState.Done
            return
        }
        setupStarted = true

        viewModelScope.launch {
            try {
                // ── 1. Database ──────────────────────────────────────────────
                if (!QuranDownloadManager.isDbReady(context)) {
                    runStep(context.getString(R.string.downloading_quran_data)) { onProg, onExt ->
                        QuranDownloadManager.setupDb(context, onProg, onExt)
                    }
                }
                // ── 2. Quran JSON ────────────────────────────────────────────
                if (!QuranDownloadManager.isJsonReady(context)) {
                    runStep(context.getString(R.string.downloading_quran_data)) { onProg, onExt ->
                        QuranDownloadManager.setupJson(context, onProg, onExt)
                    }
                }
                // ── 3. Fonts (only when rendering pages) ─────────────────────
                if (needsFonts && !QuranDownloadManager.isFontsReady(context, versionNumber)) {
                    runStep(context.getString(R.string.downloading_quran_fonts)) { onProg, onExt ->
                        QuranDownloadManager.setupFonts(context, versionNumber, onProg, onExt)
                    }
                }

                state = QuranSetupState.Done

            } catch (e: Exception) {
                state = QuranSetupState.Error(e.message ?: context.getString(R.string.error_while_downloading_files))
                setupStarted = false   // allow retry
            }
        }
    }

    /** Resets state so [checkAndSetup] can be called again after an error. */
    fun retry(context: Context, versionNumber: Int, needsFonts: Boolean = true) {
        setupStarted = false
        state = QuranSetupState.Idle
        checkAndSetup(context, versionNumber, needsFonts)
    }

    // ─────────────────────────────────────────────────────────────────────────

    private suspend fun runStep(
        label: String,
        block: suspend (
            onProgress: suspend (Int) -> Unit,
            onExtracting: suspend () -> Unit,
        ) -> Unit,
    ) {
        state = QuranSetupState.Downloading(label, 0)
        block(
            { pct -> state = QuranSetupState.Downloading(label, pct) },
            {       state = QuranSetupState.Extracting },
        )
    }
}
