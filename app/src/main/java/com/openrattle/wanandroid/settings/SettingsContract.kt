package com.openrattle.wanandroid.settings

import com.openrattle.base.model.ThemeMode

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.FOLLOW_SYSTEM,
    val isEyeProtectionEnabled: Boolean = false
)

sealed class SettingsIntent {
    data class SetThemeMode(val mode: ThemeMode) : SettingsIntent()
    data class SetEyeProtectionEnabled(val enabled: Boolean) : SettingsIntent()
}

sealed class SettingsEffect {
    data class ShowMessage(val message: String) : SettingsEffect()
}
