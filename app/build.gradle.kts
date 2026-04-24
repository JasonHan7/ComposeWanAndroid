plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.openrattle.wanandroid"
    compileSdk = 35

    // 根据 APP_VERSION_NAME (x.y.z) 自动计算唯一递增的 versionCode
    //
    // versionName = "1.0.0" -> versionCode = 10000
    // versionName = "1.2.11" -> versionCode = 10211
    // versionName = "2.10.5" -> versionCode = 21005
    val appVersionName = project.property("APP_VERSION_NAME").toString()
    val appVersionCode = appVersionName.split(".").let {
        if (it.size >= 3) {
            it[0].toInt() * 10000 + it[1].toInt() * 100 + it[2].toInt()
        } else 1
    }

    defaultConfig {
        applicationId = "com.openrattle.wanandroid"
        minSdk = 24
        targetSdk = 35
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // 本地打包 APK 命名规则同步 GitHub Actions
    applicationVariants.all {
        val variant = this
        val buildTypeName = variant.buildType.name
        variant.outputs.all {
            val branch = try {
                val process = Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD")
                process.waitFor()
                process.inputStream.bufferedReader().use { it.readText() }.trim().replace("/", "_")
            } catch (e: Exception) {
                "unknown"
            }
            val fileName = "WanAndroid_v${appVersionName}_${branch}_${buildTypeName}.apk"
            
            // 显式转型以避免 KTS 作用域歧义
            (this as com.android.build.gradle.api.ApkVariantOutput).outputFileName = fileName
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    lint {
        disable += listOf("UnusedMaterial3ScaffoldPaddingParameter")
        abortOnError = false
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Core 模块
    implementation(project(":core"))
    // Common-UI 模块
    implementation(project(":common-ui"))

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.material)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.coil.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
