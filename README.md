# KMP Showcase

A production-ready Kotlin Multiplatform demo app showcasing modern mobile development with Compose Multiplatform, clean architecture, and native platform integrations.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-Multiplatform-4285F4.svg?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
![Android](https://img.shields.io/badge/Android-36-3DDC84.svg?logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-16-000000.svg?logo=apple&logoColor=white)

**95% shared code** across Android & iOS

---

<table>
<tr>
<td style="width:50%">

### 🎨 UI & Navigation
- Compose Multiplatform
- Material 3 + Dark Mode
- Navigation3 + Deep Links
- 40+ Components

</td>
<td style="width:50%">

### 📱 Platform APIs
- Biometrics (Face ID / Fingerprint)
- Camera & Gallery
- QR/Barcode Scanner
- Location & Permissions

</td>
</tr>
<tr>
<td style="width:50%">

### 🔌 Data & Network
- Ktor 3 HTTP Client
- SQLDelight Database
- DataStore Preferences
- Coil Image Loading

</td>
<td style="width:50%">

### 🔔 Notifications
- Push (FCM / APNs)
- Local Notifications
- Notification Channels
- Permission Handling

</td>
</tr>
</table>

---

## Tech Stack

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Koin](https://img.shields.io/badge/Koin-F7A91E?logoColor=white)
![Ktor](https://img.shields.io/badge/Ktor-7F52FF?logoColor=white)
![SQLDelight](https://img.shields.io/badge/SQLDelight-005C99?logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?logo=firebase&logoColor=black)
![Detekt](https://img.shields.io/badge/Detekt-6F42C1?logoColor=white)
![Mokkery](https://img.shields.io/badge/Mokkery-FF6B6B?logoColor=white)
![Roborazzi](https://img.shields.io/badge/Roborazzi-00C853?logoColor=white)

---


## Screenshots

<table>
<tr>
<td><img src="androidApp/src/test/snapshots/images/com.mk.kmpshowcase.presentation.screen.login.LoginScreenScreenshotTest_LoginUiState_0_light.png" width="180" alt="Login"/></td>
<td><img src="androidApp/src/test/snapshots/images/com.mk.kmpshowcase.presentation.screen.home.HomeScreenScreenshotTest_HomeUiState_0_light.png" width="180" alt="Home"/></td>
<td><img src="androidApp/src/test/snapshots/images/com.mk.kmpshowcase.presentation.screen.database.DatabaseScreenScreenshotTest_DatabaseUiState_1_light.png" width="180" alt="Database"/></td>
<td><img src="androidApp/src/test/snapshots/images/com.mk.kmpshowcase.presentation.screen.calendar.CalendarScreenScreenshotTest_CalendarUiState_1_light.png" width="180" alt="Calendar"/></td>
</tr>
<tr>
<td style="text-align:center">Login</td>
<td style="text-align:center">Home</td>
<td style="text-align:center">Database</td>
<td style="text-align:center">Calendar</td>
</tr>
<tr>
<td><img src="androidApp/src/test/snapshots/images/com.mk.kmpshowcase.presentation.screen.networking.NetworkingScreenScreenshotTest_NetworkingUiState_1_light.png" width="180" alt="Networking"/></td>
<td><img src="androidApp/src/test/snapshots/images/com.mk.kmpshowcase.presentation.screen.storage.StorageScreenScreenshotTest_StorageUiState_0_light.png" width="180" alt="Storage"/></td>
<td><img src="androidApp/src/test/snapshots/images/com.mk.kmpshowcase.presentation.screen.notifications.NotificationsScreenScreenshotTest_NotificationsUiState_0_light.png" width="180" alt="Notifications"/></td>
<td><img src="androidApp/src/test/snapshots/images/com.mk.kmpshowcase.presentation.screen.settings.SettingsScreenScreenshotTest_SettingsState_0_light.png" width="180" alt="Settings"/></td>
</tr>
<tr>
<td style="text-align:center">Networking</td>
<td style="text-align:center">Storage</td>
<td style="text-align:center">Notifications</td>
<td style="text-align:center">Settings</td>
</tr>
</table>

---

## Architecture

```
Presentation  →  Domain  →  Data
  (UI/VM)       (UseCase)   (Repository)
```

---

## Quick Start

```bash
# Android
./gradlew :androidApp:installDebug

# iOS
open iosApp/iosApp.xcodeproj
```

---

## Project Structure

```
androidApp/     Android app
iosApp/         iOS app (SwiftUI shell)
shared/         Shared KMP module (UI, domain, data)
```

---

## Roadmap

- [ ] Pagination
- [ ] Maps
- [ ] Video player
- [ ] Offline-first
