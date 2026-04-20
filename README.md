# QuranSDK

An Android SDK for rendering the Holy Quran (Mushaf) with full customization — canvas-based page rendering, word-level interaction, bookmarks, search, and runtime asset downloading.

[![](https://jitpack.io/v/Mostafa-hashim-Quran-iqraa/QuranSDK.svg)](https://jitpack.io/#Mostafa-hashim-Quran-iqraa/QuranSDK)

---

## Features

- 🕌 Mushaf rendering (604 pages)
- 🎨 Full color customization (background, font, highlights, headers)
- 🔤 Two print versions: King Fahd 1441 Prints Normal & Colored
- 📖 Aya-level and Word-level highlight modes
- 🔖 Bookmark support with highlight color
- 🔍 Arabic text search
- 📚 Juz index and Surah list
- ⬇️ Automatic runtime asset download (DB + JSON + Fonts) with progress UI
- 🔁 Retry on download failure

---

## Requirements

| Item | Minimum |
|------|---------|
| Android | API 26 (Oreo) |
| Kotlin | 2.0+ |
| Java | 21 |
| Jetpack Compose BOM | 2024+ |
| Hilt | 2.5+ |

---

## Installation

### Step 1 — Add JitPack to your repositories

In your **root** `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }   // ← add this
    }
}
```

### Step 2 — Add the dependency

In your **app** `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.Mostafa-hashim-Quran-iqraa.QuranSDK:quranLib:LATEST_VERSION")
}
```

> Replace `LATEST_VERSION` with the latest release tag from [JitPack](https://jitpack.io/#Mostafa-hashim-Quran-iqraa/QuranSDK).

---

## Setup

### 1. Add INTERNET permission

In your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 2. Enable Hilt

The SDK uses Hilt for dependency injection. Make sure your app is set up with Hilt:

**`build.gradle.kts` (app)**:
```kotlin
plugins {
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

dependencies {
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-android-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
}
```

**Application class**:
```kotlin
@HiltAndroidApp
class MyApplication : Application()
```

**Activity**:
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity()
```

---

## Basic Usage

### Wrap your screen with `QuranDataGuard`

`QuranDataGuard` automatically downloads the required assets (database, JSON data, fonts) on first launch, shows a progress screen, and only renders your content once everything is ready.

```kotlin
@Composable
fun MyScreen() {
    QuranDataGuard(versionNumber = QuranConstants.VERSION_KING_FAHD_1441) {
        QuranPageCanvasModeScreen(
            quranPagesVersion = QuranConstants.VERSION_KING_FAHD_1441,
        )
    }
}
```

For screens that only need data (no page rendering — e.g., search, bookmarks):

```kotlin
QuranDataGuard(
    versionNumber = QuranConstants.VERSION_KING_FAHD_1441,
    needsFonts = false,   // skip font download
) {
    BookmarksScreen()
}
```

---

## `QuranPageCanvasModeScreen` Parameters

```kotlin
@Composable
fun QuranPageCanvasModeScreen(
    quranPagesVersion: Int = QuranConstants.VERSION_KING_FAHD_1441,
    isReversePager: Boolean = false,
    pageBackground: Color = Color.White,
    fontColor: Color = Color.Black,
    suraHeaderColor: Color = GreenDark,
    suraNameColor: Color = GreenDark,
    highlightColor: Color = colorPrimaryMoreLight,
    ayahNumberColor: Color = Color.Red,
    highlightType: Int = QuranConstants.HIGHLIGHT_TYPE_AYA,
    isSurahClickable: Boolean = false,
    isJuzClickable: Boolean = false,
    isFontBold: Boolean = false,
    pageToOpen: Int = 0,
    ayaNumberInSuraToHighlight: Int = -1,
    surahIdToHighlight: Int = -1,
    bookmarkedAyas: List<Pair<Int, Int>> = emptyList(),
    bookmarkHighlightColor: Color = Color(0x550073C9),
    errorWordLocations: Set<String> = emptySet(),
    errorHighlightColor: Color = Color(0xFFE53935),
    onClickJuzName: (ChapterModel) -> Unit = {},
    onClickSurahName: (SurahModel) -> Unit = {},
    onWordLongPressed: (highlightType: Int, WordModel, RectF, Int?, List<WordModel>, Int) -> Unit = { _, _, _, _, _, _ -> },
    onWordClicked: (WordModel, Int) -> Unit = { _, _ -> },
    onPageTap: () -> Unit = {},
)
```

### Parameter Reference

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `quranPagesVersion` | `Int` | `VERSION_KING_FAHD_1441` | Print version. Use `QuranConstants.VERSION_KING_FAHD_1441` or `VERSION_KING_FAHD_1441_COLORED` |
| `isReversePager` | `Boolean` | `false` | Reverses pager direction (right-to-left) |
| `pageBackground` | `Color` | `White` | Background color of each page |
| `fontColor` | `Color` | `Black` | Main Quran text color |
| `suraHeaderColor` | `Color` | `GreenDark` | Color of the Surah header bar |
| `suraNameColor` | `Color` | `GreenDark` | Color of the Surah name ligature |
| `highlightColor` | `Color` | `colorPrimaryMoreLight` | Color used to highlight the selected Aya/Word |
| `ayahNumberColor` | `Color` | `Red` | Color of the Aya number glyph |
| `highlightType` | `Int` | `HIGHLIGHT_TYPE_AYA` | `QuranConstants.HIGHLIGHT_TYPE_AYA` or `HIGHLIGHT_TYPE_WORD` |
| `isSurahClickable` | `Boolean` | `false` | Whether tapping a Surah header triggers `onClickSurahName` |
| `isJuzClickable` | `Boolean` | `false` | Whether tapping a Juz header triggers `onClickJuzName` |
| `isFontBold` | `Boolean` | `false` | Renders Quran text in bold weight |
| `pageToOpen` | `Int` | `0` | Page number to open initially (1–604) |
| `ayaNumberInSuraToHighlight` | `Int` | `-1` | Aya number (within its Surah) to highlight on open |
| `surahIdToHighlight` | `Int` | `-1` | Surah ID for the aya to highlight |
| `bookmarkedAyas` | `List<Pair<Int,Int>>` | `emptyList()` | List of `(surahId, ayaNumber)` pairs to mark as bookmarked |
| `bookmarkHighlightColor` | `Color` | semi-transparent blue | Background color for bookmarked ayas |
| `errorWordLocations` | `Set<String>` | `emptySet()` | Set of word location strings to highlight as errors |
| `errorHighlightColor` | `Color` | Red | Text color for error-marked words |
| `onClickJuzName` | `(ChapterModel) -> Unit` | `{}` | Called when a Juz header is tapped |
| `onClickSurahName` | `(SurahModel) -> Unit` | `{}` | Called when a Surah name is tapped |
| `onWordLongPressed` | `(Int, WordModel, RectF, Int?, List<WordModel>, Int) -> Unit` | `{}` | Called on long-press; receives `(highlightType, word, boundingRect, ayaId, allWordsInAya, pageNumber)` |
| `onWordClicked` | `(WordModel, Int) -> Unit` | `{}` | Called when a word is tapped; receives `(word, pageNumber)` |
| `onPageTap` | `() -> Unit` | `{}` | Called when the page background is tapped (outside any word) |

---

## Print Versions

| Constant | Value | Description |
|----------|-------|-------------|
| `QuranConstants.VERSION_KING_FAHD_1441` | `1` | King Fahd Mushaf — standard black text |
| `QuranConstants.VERSION_KING_FAHD_1441_COLORED` | `2` | King Fahd Mushaf — color-coded tajweed |

---

## Highlight Types

| Constant | Value | Description |
|----------|-------|-------------|
| `QuranConstants.HIGHLIGHT_TYPE_AYA` | `1` | Highlights the entire aya on long-press |
| `QuranConstants.HIGHLIGHT_TYPE_WORD` | `2` | Highlights individual words on long-press |

---

## Full Example

```kotlin
@AndroidEntryPoint
class QuranActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuranDataGuard(versionNumber = QuranConstants.VERSION_KING_FAHD_1441) {
                QuranPageCanvasModeScreen(
                    quranPagesVersion      = QuranConstants.VERSION_KING_FAHD_1441,
                    pageBackground         = Color(0xFFFDF8F2),
                    fontColor              = Color.Black,
                    suraHeaderColor        = Color(0xFF0073C9),
                    suraNameColor          = Color(0xFF0073C9),
                    highlightColor         = Color(0xFFDBEBF7),
                    ayahNumberColor        = Color(0xFFE53935),
                    highlightType          = QuranConstants.HIGHLIGHT_TYPE_WORD,
                    isSurahClickable       = true,
                    isJuzClickable         = true,
                    isFontBold             = false,
                    pageToOpen             = 1,
                    bookmarkedAyas         = listOf(Pair(1, 1), Pair(2, 255)),
                    bookmarkHighlightColor = Color(0x550073C9),
                    onClickSurahName = { surah ->
                        Log.d("Quran", "Surah clicked: ${surah.name_ar}")
                    },
                    onWordLongPressed = { type, word, rect, ayaId, allWords, page ->
                        Log.d("Quran", "Long pressed: ${word.wordText} on page $page")
                    },
                    onWordClicked = { word, page ->
                        Log.d("Quran", "Clicked: ${word.wordText} on page $page")
                    },
                    onPageTap = {
                        // toggle UI overlays
                    },
                )
            }
        }
    }
}
```

---

## How Asset Downloading Works

On first launch, `QuranDataGuard` automatically:

1. **Downloads** the Quran database (`.db`), JSON data, and font pack from the cloud
2. **Shows a progress screen** with a progress bar and percentage
3. **Extracts** the zip files and saves them to internal storage
4. **Renders your content** once all assets are ready

On subsequent launches, the assets are already in internal storage and the content is shown immediately (no download screen).

If a download fails, an error screen is shown with a **retry button**.

---

## License

```
Copyright 2024 Mostafa Hashim / Quran Iqraa

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
