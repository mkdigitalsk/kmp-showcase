# kmp-showcase — Kotlin Multiplatform Component Showcase

A **KMP portfolio app** demonstrating multiplatform capabilities with **shared business/UI code** and **native shells** for Android and iOS.

## TL;DR

- **Purpose**: Portfolio app showcasing KMP capabilities — each feature is a self-contained demo.
- **KMP shared**: UI (Compose Multiplatform), Navigation3, DI (Koin), networking (Ktor 3), database (SQLDelight), JSON (kotlinx-serialization).
- **Android**: Material 3, Activity Compose, edge‑to‑edge, Firebase Analytics, FCM push notifications.
- **iOS**: Compose MPP UI hosted in Swift/SwiftUI shell, safe‑area support, Darwin HTTP engine, Firebase Analytics, APNs push notifications.

---

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin |
| UI | Compose Multiplatform |
| Navigation | Navigation3 |
| DI | Koin |
| Networking | Ktor 3 (OkHttp / Darwin) |
| Database | SQLDelight |
| Images | Coil 3 |
| Analytics | Firebase Analytics |
| Push Notifications | FCM (Android) / APNs (iOS) |
| Testing | kotlin.test + Mokkery |
| Code Quality | Detekt + Compose rules |

See `gradle/libs.versions.toml` for versions.

---

## Module Structure

```
root
├─ androidApp/    # Android app (Compose, Material3)
├─ iosApp/        # iOS app (Swift/SwiftUI host)
└─ shared/        # KMP shared code
   └─ src/
      ├─ commonMain/     # Shared (UI, domain, data, DI)
      ├─ commonTest/     # Unit tests
      ├─ androidMain/    # Android implementations
      └─ iosMain/        # iOS implementations
```

---

## Architecture

```mermaid
graph LR
    Presentation[Presentation\nUI + ViewModel] --> Domain[Domain\nUseCases + Models]
    Domain --> Data[Data\nRepositories + DataSources]
```

**MVVM Clean** — Presentation → Domain → Data

---

## Screens

| Screen | Description |
|--------|-------------|
| Login | Email/password with biometric authentication (Fingerprint/Face ID) |
| Register | User registration with validation |
| Home | Feature catalog with cards |
| UI Components | 40+ reusable components (buttons, inputs, dialogs, etc.) |
| Networking | Ktor HTTP client demo |
| Storage | Session vs Persistent storage |
| Database | SQLDelight with notes CRUD, search & sort |
| Platform APIs | Share, dial, links, email, clipboard, location, biometrics |
| Scanner | QR/barcode generator & scanner |
| Calendar | Date range picker with disabled dates |
| Notifications | Push notifications, local notifications, permission handling |
| Settings | Theme, Language, profile photo picker |

---

## Commands

```bash
# Run tests
./gradlew :shared:testAndroidHostTest

# Run detekt
./gradlew detekt
```

---

## Implemented Features

### Core
- ✅ Compose Multiplatform UI with Material 3
- ✅ Navigation3 with floating nav bar
- ✅ Koin dependency injection
- ✅ Safe area / edge-to-edge
- ✅ Unit tests (kotlin.test + Mokkery)
- ✅ String resources (EN/SK)
- ✅ Global snackbar (themed)
- ✅ Login/Register with form validation
- ✅ Biometric authentication (Fingerprint on Android, Face ID on iOS)

### Networking & Data
- ✅ Ktor HTTP client with JSON
- ✅ DataStore / Preferences
- ✅ SQLDelight local database
- ✅ Coil 3 image loading

### Device Features
- ✅ Camera & Image picker (profile photo)
- ✅ QR/Barcode scanner & generator
- ✅ Biometrics (Fingerprint on Android, Face ID on iOS)
- ✅ Location / GPS
- ✅ Permissions handling
- ✅ Platform routers (share, dial, links, email, clipboard)

### UI Components
- ✅ Buttons, Cards, Dialogs, Bottom Sheet
- ✅ TextField, Switch, Radio, Checkbox, Chips
- ✅ Slider, Progress indicators, Dividers
- ✅ Segmented Button, Badge, Menu
- ✅ SearchField with debounce (database search & sort)
- ✅ Calendar (date range picker, disabled dates, month navigation)
- ✅ Typography, Spacers

### Notifications
- ✅ Push notifications (FCM on Android, APNs on iOS)
- ✅ Local notifications with channels
- ✅ Notification permission handling
- ✅ Open notification settings

### Quality & Build
- ✅ Detekt + Compose rules
- ✅ GitHub Actions CI
- ✅ Firebase Analytics (screen tracking)
- ✅ Crashlytics (crashes + non-fatal exceptions)
- ✅ ProGuard / R8
- ✅ Dark mode & Localization

---

## Roadmap

- [x] Form validation (Login/Register)
- [x] Date/Time pickers (Calendar with range selection)
- [x] Push notifications (FCM/APNs)
- [ ] Pagination (infinite scroll)
- [ ] Deep links
- [ ] Maps integration
- [ ] Video player
- [ ] Offline-first sync

---
