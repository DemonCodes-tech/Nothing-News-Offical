# Nothing News

A distraction-free Android news reader, built with Kotlin and Jetpack Compose. Pick between a stark, dot-matrix Nothing OS look or a Material You theme that adapts to your wallpaper.

No ads. No accounts. No tracking.

## Features

- **Two themes, one app** — switch between a monochrome Nothing OS–style UI (dot-matrix type, glyph accents) and Material You dynamic color.
- **Live wire feed** — pulls directly from UN News and NPR International on an hourly cycle.
- **Save for later** — articles you save are stored locally (Room) so you can read them offline.
- **Zero sensationalism** — UI and copy are built around plain chronological facts over clickbait phrasing.
- **No ads, no accounts, no tracking cookies.**

## Tech stack

- Kotlin, [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room](https://developer.android.com/training/data-storage/room) for local storage
- MVVM: `ViewModel` → `Repository` → (`Room` local / remote feed service)

## Getting started

**Prerequisites:** [Android Studio](https://developer.android.com/studio)

1. Clone the repo.
2. Open it in Android Studio and let Gradle sync.
3. Run on an emulator or device.

## Project structure

```
app/src/main/java/com/example/
├── data/
│   ├── local/        # Room database, DAO, entities
│   ├── model/         # Data models
│   ├── remote/         # Live RSS feed service
│   └── repository/     # NewsRepository
├── ui/
│   ├── components/      # Reusable Compose components
│   ├── screens/          # Feed, article detail, saved library, settings
│   ├── theme/              # Color, type, theme definitions
│   └── viewmodel/          # NewsViewModel
└── MainActivity.kt
```
## Status

Solo project, still actively worked on. Open an issue if something's broken.

---
Made by Demon 🖤
