# kmp-showcase — Kotlin Multiplatform Component Showcase

A **KMP portfolio app** demonstrating multiplatform capabilities with **shared business/UI code** and **native shells** for Android and iOS.
Serves as a living reference for KMP features: device APIs, UI components, storage, networking, and platform integrations.

## TL;DR

- **Purpose**: Portfolio app showcasing KMP capabilities — each feature is a self-contained demo.
- **KMP shared**: UI (Compose Multiplatform), Navigation3, DI (Koin), networking (Ktor 3), JSON (kotlinx-serialization), coroutines, datetime.
- **Android**: Material 3, Activity Compose, edge‑to‑edge.
- **iOS**: Compose MPP UI hosted in Swift/SwiftUI shell, safe‑area support, Darwin HTTP engine.

---

## Features

- ✅ **Bottom bar** navigation (tabs) with `Navigation3`
- ✅ **Safe area / edge‑to‑edge** paddings handled for both Android & iOS
- ✅ **Koin** modules for easy dependency wiring
- ✅ **Ktor 3** client with ContentNegotiation(JSON), Logging
- ✅ **Coil 3** image loading (with Ktor network integration + SVG support)
- ✅ **Resource/version catalog** via `libs.versions.toml`
- ✅ **Unit tests** in commonTest (kotlin.test + Mokkery + coroutines-test)
- ✅ **Reusable UI components** (buttons, cards, text styles, dialogs, toolbars)
- ✅ **Platform routers** (share, dial, external links) via expect/actual

---

## Tech Stack

- **Language**: Kotlin
- **UI**: Compose Multiplatform
- **Navigation**: Navigation3
- **Lifecycle**: Lifecycle ViewModel/Runtime
- **DI**: Koin
- **Networking**: Ktor
- **Serialization**: kotlinx-serialization
- **Coroutines**: kotlinx-coroutines
- **Date/Time**: kotlinx-datetime
- **Images**: Coil 3
- **Testing**: kotlin.test + Mokkery + kotlinx-coroutines-test
- **Code Quality**: Detekt + Compose rules

See `gradle/libs.versions.toml` for current versions.

---

## Module Structure

```
root
├─ androidApp/    # Android application (Compose, Material3, Android entry point)
├─ iosApp/        # iOS application (Swift/SwiftUI host, LaunchScreen)
└─ shared/        # KMP shared code
   └─ src/
      ├─ commonMain/     # Shared code (UI, domain, data, DI, navigation)
      ├─ commonTest/     # Shared unit tests
      ├─ androidMain/    # Android-specific implementations
      └─ iosMain/        # iOS-specific implementations
```

### Shared module packages:

- **data/** — DTOs, repositories, network client (Ktor)
- **domain/** — Models, repository interfaces, use cases
- **presentation/** — Compose UI screens, components, theme, navigation
- **di/** — Koin modules
- **util/** — Logger (expect/actual)

---

## Screens

| Screen | Description |
|--------|-------------|
| Home | Feature list / Component showcase menu |
| Explore | Discovery/browse screen |
| Profile | User profile screen |
| Detail | Feature demo detail view |

---

## Architecture & Conventions

- **UI**: Compose Multiplatform with Material3 styling; safe‑area via Compose WindowInsets APIs and platform wrappers.
- **Navigation**: `Navigation3` tabs. Each tab manages its own navigation state.
- **DI**: `Koin` modules in `shared` (data, domain, presentation modules). Android initializes Koin in `Application`, iOS during app launch.
- **Networking**: `Ktor 3`
    - Android → OkHttp engine
    - iOS → Darwin engine
    - JSON via `kotlinx.serialization` + `ContentNegotiation`
    - Logging via `ktor-client-logging`
- **Images**: `Coil 3` with `coil-network-ktor3` and SVG support
- **Coroutines**: Structured concurrency; never block the main thread.

---

## Testing

Tests are located in `shared/src/commonTest/` and run on JVM via `androidHostTest`.

- **kotlin.test** — Multiplatform test framework
- **Mokkery** — Multiplatform mocking library
- **kotlinx-coroutines-test** — Coroutine testing utilities

Run tests:
```bash
./gradlew :shared:testAndroidHostTest
```

---

## Code Quality

Static analysis with **Detekt** + **Compose rules**.

Run analysis:
```bash
./gradlew detekt
```

Run with auto-correct:
```bash
./gradlew detekt --auto-correct
```

Configuration: `config/detekt/detekt.yml`

---

## Logging

- Common logging facade in `shared` (expect/actual).
- Android → `Logcat`, iOS → `NSLog`.
- Network logging via `Ktor` (already wired).

---

## Roadmap / Feature Showcase

This app serves as a **KMP components portfolio** — a living demo of multiplatform capabilities.

### Core Architecture
- [x] Compose Multiplatform UI
- [x] Navigation3 with bottom bar
- [x] Koin dependency injection
- [x] Safe area / edge-to-edge handling
- [x] Platform logging (expect/actual)
- [x] Unit tests (kotlin.test + Mokkery)

### Networking
- [x] Ktor HTTP client
- [x] REST API with JSON serialization
- [x] Network logging
- [ ] WebSockets
- [ ] Refresh token handling
- [ ] Offline-first sync

### UI Components
- [x] Buttons (contained, outlined, text)
- [x] Cards
- [x] Dialogs / Alerts
- [x] Toolbar
- [x] Text styles (typography)
- [x] Spacers
- [x] Loading indicators
- [x] Checkbox
- [x] Dividers
- [x] Coil image loading (+ SVG)
- [ ] Bottom sheets
- [ ] Snackbars / Toasts
- [ ] Pull to refresh
- [ ] Swipe actions
- [ ] Animations & transitions
- [ ] Charts / Graphs
- [ ] Skeleton loaders
- [ ] Onboarding / Tutorial screens

### Platform Routers
- [x] Share content
- [x] Dial phone number
- [x] Open external links
- [ ] Open email client
- [ ] Open maps

### Device Features
- [ ] Camera (photo/video capture)
- [ ] Barcode / QR code scanner
- [ ] Local notifications
- [ ] Push notifications (Firebase)
- [ ] Biometrics (fingerprint, Face ID)
- [ ] Location / GPS
- [ ] Permissions handling
- [ ] File picker
- [ ] Document scanner
- [ ] Contacts access
- [ ] Calendar integration
- [ ] Haptics / Vibration
- [ ] Flashlight
- [ ] Device info (battery, network state)

### Media
- [ ] Image picker & cropper
- [ ] Video player
- [ ] PDF viewer
- [ ] WebView
- [ ] Maps integration

### Data & Storage
- [ ] Local database (SQLDelight)
- [ ] DataStore / Preferences
- [ ] File storage
- [ ] Encrypted storage
- [ ] Caching strategies

### Auth
- [ ] OAuth / Social login
- [ ] Firebase Auth
- [ ] Biometric auth gate

### Forms & Input
- [ ] Form validation patterns
- [ ] Text field types (email, phone, password, OTP)
- [ ] Search with debounce
- [ ] Filters & sorting
- [ ] Date/Time pickers
- [ ] Dropdown / Spinner
- [ ] Copy to clipboard

### Lists & Data Patterns
- [ ] Pagination (infinite scroll)
- [ ] Empty states
- [ ] Error states with retry
- [ ] Swipe to delete/archive
- [ ] Drag and drop reordering
- [ ] Sticky headers
- [ ] Multi-select mode

### Platform Integration
- [ ] Deep links / Universal links
- [ ] App shortcuts
- [ ] Widgets (Android/iOS)
- [ ] Background tasks
- [ ] In-app updates
- [ ] In-app review prompt
- [ ] App Tracking Transparency (iOS)

### Security
- [ ] SSL pinning
- [ ] Encrypted storage
- [ ] Root / Jailbreak detection
- [ ] Screenshot prevention (sensitive screens)
- [ ] ProGuard / R8 obfuscation
- [ ] Biometric + PIN fallback

### Monitoring & Analytics
- [ ] Firebase Analytics
- [ ] Crashlytics integration
- [ ] Performance monitoring

### Build & CI
- [ ] Release build variant, signing config
- [x] CI: GitHub Actions (tests + detekt)
- [ ] Fastlane integration
- [x] Detekt + Compose rules
- [ ] Test coverage reports

### Code Quality
- [ ] Architecture diagram in README
- [ ] Result type / sealed class error handling
- [ ] Loading / Error / Empty states pattern
- [ ] Graceful offline behavior
- [ ] Consistent logging throughout
- [ ] Meaningful git commit history

### Documentation
- [ ] Screenshots / GIFs of features
- [ ] Architecture decision records (ADRs)
- [ ] Blog posts explaining key decisions
- [ ] Inline KDoc for public APIs

### Distribution
- [ ] Play Store listing (internal/unlisted)
- [ ] TestFlight distribution
- [ ] QR codes in README to download
- [ ] Firebase App Distribution setup

### Accessibility
- [ ] Screen reader support (TalkBack/VoiceOver)
- [ ] Dynamic text scaling

### Bonus Features
- [ ] Dark mode toggle
- [ ] Localization (multi-language)
- [ ] Connectivity monitoring (online/offline banner)
- [ ] Material You dynamic colors (Android)
- [ ] On-device ML (image classification / OCR)
- [ ] Bluetooth / NFC
- [ ] Speech to text / Text to speech

---
