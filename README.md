# 🚀 Speed Drawer - Native Android App

A high-performance, lightweight native Android app drawer built with Kotlin and Jetpack Compose. Originally converted from Flutter to achieve better performance and smaller APK size.

## ✨ Features

- **Fast App Discovery** - Lightning-fast app search and launch
- **Fuzzy Search** - Smart search with fuzzy matching
- **Usage Tracking** - Tracks most-used apps for quick access
- **Favorites** - Pin your favorite apps for instant access
- **Material3 Design** - Modern UI with dark/light theme support
- **Haptic Feedback** - Satisfying tactile responses
- **Performance Optimized** - Native performance with ProGuard optimization

## 🏗️ Architecture

- **MVVM Pattern** - Clean separation of concerns
- **Jetpack Compose** - Modern declarative UI
- **StateFlow** - Reactive state management
- **Room Database** - Local data persistence
- **DataStore** - Modern preferences storage
- **Hilt** - Dependency injection
- **Material3** - Latest Material Design system

## 🔧 Tech Stack

- **Language**: Kotlin 100%
- **UI**: Jetpack Compose + Material3
- **Architecture**: MVVM + StateFlow
- **Database**: Room
- **DI**: Hilt
- **Build**: Gradle KTS
- **CI/CD**: GitHub Actions

## 📱 Performance

Compared to the original Flutter version:
- **~40MB smaller APK** (no Flutter engine overhead)
- **Faster startup time** (native performance)
- **Better battery efficiency** (no framework overhead)
- **Direct Android API access**

## 🚀 Building

### GitHub Actions (Recommended)
All builds happen automatically on GitHub:
- **Push to main** → Debug APK
- **Create tag `v*.*.*`** → Signed release APK/AAB

### Local Build (Optional)
```bash
./gradlew assembleDebug
./gradlew assembleRelease
```

## 📦 Installation

1. Download the latest APK from [Releases](https://github.com/yamodiji/CONTOKOT/releases)
2. Install on your Android device
3. Set as default launcher (optional)

## 🔐 Permissions

- **QUERY_ALL_PACKAGES** - To discover installed apps
- **VIBRATE** - For haptic feedback
- **INTERNET** - For future cloud features (optional)

## 🛠️ Development

```bash
# Clone repository
git clone https://github.com/yamodiji/CONTOKOT.git

# Build debug APK
./gradlew :app:assembleDebug

# Run tests
./gradlew :app:test
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Built with ❤️ using Kotlin and Jetpack Compose** 