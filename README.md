# SenpwaiA

SenpwaiA is an Android application for downloading anime from AnimePahe. It's a complete rewrite of the original Senpwai/Senpcli Python CLI tool in Kotlin with modern Android development practices.

## Features

- Search for anime on AnimePahe
- Download episodes with configurable quality (360p, 480p, 720p, 1080p)
- Choose between sub/dub versions
- Select episode ranges to download
- Background downloads with WorkManager and Foreground Service
- Material 3 design with dark/light theme support
- Settings to customize download preferences

## Architecture

- **MVVM**: Model-View-ViewModel architecture for clean separation of concerns
- **Jetpack Compose**: Modern Android UI toolkit for declarative UI
- **WorkManager**: For reliable background task execution
- **DataStore**: For preferences storage
- **Retrofit**: For network requests
- **JSoup**: For HTML parsing

## Project Structure

```
app/
├── src/main/
│   ├── java/com/sleepy/senpwaia/
│   │   ├── data/          # Network, database, repository
│   │   ├── models/        # Data classes
│   │   ├── services/      # Download services and managers
│   │   ├── ui/            # Compose UI components
│   │   │   ├── components/ # Reusable UI components
│   │   │   ├── home/      # Home screen
│   │   │   ├── search/    # Search screen
│   │   │   ├── details/   # Anime details screen
│   │   │   ├── download/  # Download screens
│   │   │   ├── settings/  # Settings screen
│   │   │   ├── theme/     # Material theme
│   │   │   └── navigation/ # Navigation graph
│   │   ├── utils/         # Utility classes
│   │   └── viewmodels/    # ViewModel classes
│   └── res/               # Resources
└── src/test/              # Unit tests
```

## Dependencies

- AndroidX libraries
- Jetpack Compose
- Retrofit for networking
- JSoup for HTML parsing
- WorkManager for background tasks
- DataStore for preferences
- Coil for image loading
- Kotlin Coroutines

## Building
### Note: Run:
             ```gradle wrapper```

To build the project:

```bash
./gradlew build
```

To create a release APK:

```bash
./gradlew assembleRelease
```

## Testing

To run unit tests:

```bash
./gradlew test
```

## ProGuard/R8

The application includes ProGuard rules for code shrinking and obfuscation in release builds, located at `app/proguard-rules.pro`.

## Permissions

The app requires the following permissions:
- INTERNET: For network requests
- WRITE_EXTERNAL_STORAGE: For saving downloaded files
- FOREGROUND_SERVICE: For download service
- POST_NOTIFICATIONS: For download progress notifications

## Notes

- This app is designed for AnimePahe only
- Network requests include appropriate headers to avoid rate limiting
- Download links are decrypted using the same algorithm as the original Python version
- The app follows Android design guidelines with Material 3 components
