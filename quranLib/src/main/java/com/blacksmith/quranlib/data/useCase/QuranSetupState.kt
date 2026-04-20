package com.blacksmith.quranlib.data.useCase

sealed class QuranSetupState {
    /** Initial state — check not started yet. */
    object Idle : QuranSetupState()

    /** Actively downloading a file. [label] describes what's being downloaded. */
    data class Downloading(val label: String, val progress: Int) : QuranSetupState()

    /** Download finished, currently extracting the zip. */
    object Extracting : QuranSetupState()

    /** All required files are ready — safe to render content. */
    object Done : QuranSetupState()

    /** A download or IO error occurred. */
    data class Error(val message: String) : QuranSetupState()
}