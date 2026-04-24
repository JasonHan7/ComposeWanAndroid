# ComposeWanAndroid
[English](./README_EN.md) | 中文
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

一个基于 **Jetpack Compose** 打造的玩安卓 (WanAndroid) 客户端，严格遵循 **MVI** 架构与 **Clean Architecture** 设计原则。

## 🛠 技术栈

*   **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
*   **架构**: MVI + Clean Architecture + [Hilt](https://dagger.dev/hilt/) (DI)
*   **异步**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) + [Flow](https://kotlinlang.org/docs/flow.html)
*   **网络**: [Ktor](https://ktor.io/) 
*   **存储**: [Room](https://developer.android.com/training/data-storage/room) (数据库) + [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (偏好设置)
*   **序列化**: [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
*   **图片加载**: [Coil](https://coil-kt.github.io/coil/)
*   **导航**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)

##  架构设计

项目采用严格的五层 Clean Architecture，每一层通过契约进行通信：

| 层级 | 职责                                        | 实现方式 |
| :--- |:------------------------------------------| :--- |
| **UI (Screen)** | 渲染 State，通过 Intent 触发交互。                  | Compose, Navigation |
| **ViewModel** | 状态管理，分发 Intent，通过 Flow 暴露 State 和 Effect。 | MviViewModel, StateFlow |
| **Domain (UseCase)** | **核心业务逻辑**。负责缓存策略、数据转换、跨 Repository 编排。   | Kotlin Coroutines |
| **Repository** | 抽象数据接口。协调 Remote 和 Local 数据源。             | Repository Pattern |
| **Data (DataSource)** | 单一数据来源。负责网络请求或数据库读写。                      | Ktor, Room |

### MVI 契约示例
每个功能模块都在 `Contract.kt` 中定义其专属契约：
*   **State**: 当前页面的唯一状态快照。
*   **Intent**: 用户的所有交互意图。
*   **Effect**: 瞬时性的副作用（如弹窗、跳转）。

## 核心功能

-  **首页**：置顶文章、Banner 轮播、仿掘金搜索栏、未读消息红点。
-  **问答 & 广场**：社区互动内容，支持文章分享。
-  **消息中心**：实时提醒，支持点击跳转至详情。
-  **收藏系统**：跨页面同步收藏状态。
-  **浏览历史**：本地持久化记录阅读过的文章。
-  **体系/导航**：结构化查看各技术频道。
-  **搜索系统**：热门词云、搜索历史。
-  **设置中心**：动态主题切换、护眼模式（全局滤镜）。
-  **阅读模式**：详情页一键净化，极简阅读体验。

## 环境配置

*   **Android Studio**: Panda4 | 2025.3.4 Canary2
*   **Kotlin**: 2.1.10
*   **Compose BOM**: 2025.02.00
*   **AGP (Android Gradle Plugin)**: 8.8.0
*   **Gradle**: gradle-9.5.0-milestone-5-bin
*   **Java**: JDK 21

## CI/CD

本项目集成了 GitHub Actions 自动化工作流：
*   **自动打包**：每次手动触发或推送以 `v*` 开头的 Tag 时，自动执行 `./gradlew assembleRelease`。
*   **自动发布**：构建成功的 APK 将自动重命名并发布至 [Releases](https://github.com/JasonHan7/ComposeWanAndroid/releases) 页面。

## ⚖️ License

```text
Copyright 2024 Jason Han

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
