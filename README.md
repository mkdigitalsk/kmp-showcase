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

- ✅ **Floating nav bar** (M3 Expressive style) with `Navigation3`
- ✅ **Safe area / edge‑to‑edge** paddings handled for both Android & iOS
- ✅ **Koin** modules for easy dependency wiring
- ✅ **Ktor 3** client with ContentNegotiation(JSON), Logging
- ✅ **Coil 3** image loading (with Ktor network integration + SVG support)
- ✅ **Resource/version catalog** via `libs.versions.toml`
- ✅ **Unit tests** in commonTest (kotlin.test + Mokkery + coroutines-test)
- ✅ **40+ reusable UI components** with App prefix convention
- ✅ **Global snackbar** with theming (Default, Success, Error, Warning)
- ✅ **Platform routers** (share, dial, external links) via expect/actual
- ✅ **String resources** for localization-ready UI

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
| Home | Feature catalog with cards linking to each showcase |
| UI Components | Comprehensive component showcase (buttons, inputs, feedback, etc.) |
| Networking | Ktor HTTP client demo (placeholder) |
| Storage | DataStore/Preferences demo (placeholder) |
| Platform APIs | Share, dial, external links demo (placeholder) |
| Settings | App settings screen |

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
- [x] Navigation3 with floating nav bar (M3 Expressive)
- [x] Koin dependency injection
- [x] Safe area / edge-to-edge handling
- [x] Platform logging (expect/actual)
- [x] Unit tests (kotlin.test + Mokkery)
- [x] String resources for localization
- [x] Global snackbar state management

### Networking
- [x] Ktor HTTP client
- [x] REST API with JSON serialization
- [x] Network logging
- [ ] WebSockets
- [ ] Refresh token handling
- [ ] Offline-first sync

### UI Components
- [x] Buttons (ContainedButton, OutlinedButton, AppTextButton)
- [x] Floating Action Button (AppFloatingActionButton)
- [x] Cards (AppCard, AppElevatedCard with click support)
- [x] Dialogs (AppAlertDialog, AppConfirmDialog)
- [x] Bottom Sheet (AppBottomSheet)
- [x] Snackbar with theming (AppSnackbar - Default, Success, Error, Warning)
- [x] TextField with clear button (AppTextField)
- [x] Switch (AppSwitch)
- [x] Radio buttons (AppRadioButton)
- [x] Checkbox (AppCheckbox)
- [x] Chips (AppFilterChip, AppAssistChip, AppInputChip, AppSuggestionChip)
- [x] Slider (AppSlider)
- [x] Progress indicators (CircularProgress, AppLinearProgress)
- [x] Dividers (AppDividerPrimary)
- [x] Toolbar (TopAppBar)
- [x] Floating Nav Bar (AppFloatingNavBar - M3 Expressive style)
- [x] Text styles / Typography (TextHeadlineMedium, TextTitleLarge, TextBodyLarge, TextBodyMedium, etc.)
- [x] Spacers (ColumnSpacer, RowSpacer)
- [x] Image loading with Coil 3 (+ SVG support)
- [x] Segmented Button (AppSegmentedButton)
- [ ] Badge
- [ ] TabBar
- [ ] Menu / Dropdown
- [ ] ListTile
- [ ] Pull to refresh
- [ ] Swipe actions
- [ ] Animations & transitions
- [ ] Charts / Graphs
- [ ] Skeleton loaders

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
- [x] DataStore / Preferences (infrastructure ready)
- [ ] Local database (SQLDelight)
- [ ] File storage
- [ ] Encrypted storage
- [ ] Caching strategies

### Auth
- [ ] OAuth / Social login
- [ ] Firebase Auth
- [ ] Biometric auth gate

### Forms & Input
- [x] TextField with clear button (AppTextField)
- [x] Switch toggle (AppSwitch)
- [x] Radio button groups (AppRadioButton)
- [x] Checkbox (AppCheckbox)
- [x] Filter/Input/Assist/Suggestion chips
- [x] Slider (AppSlider)
- [ ] Form validation patterns
- [ ] Text field types (email, phone, password, OTP)
- [ ] Search with debounce
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
