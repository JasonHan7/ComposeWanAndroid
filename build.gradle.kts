// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.library) apply false
}

subprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            when (requested.group) {
                "androidx.lifecycle" -> useVersion(libs.versions.androidxLifecycle.get())
                "androidx.activity" -> useVersion(libs.versions.androidxActivity.get())
                "androidx.appcompat" -> useVersion(libs.versions.androidxAppCompat.get())
            }
        }
    }
}
