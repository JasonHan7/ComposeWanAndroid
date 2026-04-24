# ComposeWanAndroid
English | [中文](./README.md)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

A WanAndroid client built with **Jetpack Compose**, strictly following **MVI** architecture and **Clean Architecture** design principles.

## 🛠 Tech Stack

*   **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
*   **Architecture**: MVI + Clean Architecture + [Hilt](https://dagger.dev/hilt/) (DI)
*   **Asynchronous**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) + [Flow](https://kotlinlang.org/docs/flow.html)
*   **Network**: [Ktor](https://ktor.io/)
*   **Storage**: [Room](https://developer.android.com/training/data-storage/room) (Database) + [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Preferences)
*   **Serialization**: [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
*   **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
*   **Navigation**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)

## 📐 Architecture Design

The project adopts a strict five-layer Clean Architecture, with each layer communicating through contracts:

| Layer | Responsibility | Implementation |
| :--- |:------------------------------------------| :--- |
| **UI (Screen)** | Renders State, triggers interactions via Intents. | Compose, Navigation |
| **ViewModel** | Manages state, dispatches Intents, exposes State and Effect via Flow. | MviViewModel, StateFlow |
| **Domain (UseCase)** | **Core business logic**. Responsible for cache strategy, data transformation, and cross-repository orchestration. | Kotlin Coroutines |
| **Repository** | Abstract data interface. Coordinates Remote and Local data sources. | Repository Pattern |
| **Data (DataSource)** | Single source of truth. Responsible for network requests or database read/write. | Ktor, Room |

### MVI Contract Example
Each functional module defines its own contract in `Contract.kt`:
*   **State**: A snapshot of the current page's unique state.
*   **Intent**: All user interaction intentions.
*   **Effect**: Transient side effects (e.g., dialogs, navigation).

## 📸 Core Features

-   **Home**: Top articles, Banner carousel, Juejin-style search bar, unread message badge.
-   **QA & Plaza**: Community interaction content, supports article sharing.
-   **Message Center**: Real-time notifications with click-to-detail navigation.
-   **Collection System**: Synchronized collection status across all pages.
-   **Browsing History**: Local persistence of read articles.
-   **System/Navigation**: Structured view of various technical channels.
-   **Search System**: Hot search word cloud, search history.
-   **Settings Center**: Dynamic theme switching, eye protection mode (global filter).
-   **Reader Mode**: One-click purification for details page, minimalist reading experience.

## ⚙️ Environment

*   **Android Studio**: Panda4 | 2025.3.4 Canary2
*   **Kotlin**: 2.1.10
*   **Compose BOM**: 2025.02.00
*   **AGP (Android Gradle Plugin)**: 8.8.0
*   **Gradle**: gradle-9.5.0-milestone-5-bin
*   **Java**: JDK 21

## 🤖 CI/CD

This project integrates GitHub Actions for automated workflows:
*   **Auto Build**: Automatically runs `./gradlew assembleRelease` on every manual trigger or push of tags starting with `v*`.
*   **Auto Release**: Successfully built APKs are automatically renamed and published to the [Releases](https://github.com/JasonHan7/ComposeWanAndroid/releases) page.

## ⚖️ License

```text
Copyright 2024 Jason Han

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
