package com.openrattle.wanandroid.settings

import androidx.lifecycle.viewModelScope
import com.openrattle.base.model.ThemeMode
import com.openrattle.core.MviViewModel
import com.openrattle.core.utils.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : MviViewModel<SettingsState, SettingsIntent, SettingsEffect>() {

    override fun initialState(): SettingsState = SettingsState()

    init {
        viewModelScope.launch {
            settingsManager.themeMode.collectLatest { mode ->
                updateState { it.copy(themeMode = mode) }
            }
        }
        viewModelScope.launch {
            settingsManager.isEyeProtectionEnabled.collectLatest { enabled ->
                updateState { it.copy(isEyeProtectionEnabled = enabled) }
            }
        }
    }

    override suspend fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SetThemeMode -> {
                settingsManager.setThemeMode(intent.mode)
            }
            is SettingsIntent.SetEyeProtectionEnabled -> {
                settingsManager.setEyeProtectionEnabled(intent.enabled)
            }
        }
    }
}
