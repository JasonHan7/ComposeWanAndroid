package com.openrattle.core.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.openrattle.base.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    companion object {
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val EYE_PROTECTION = booleanPreferencesKey("eye_protection")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            val modeName = preferences[THEME_MODE] ?: ThemeMode.FOLLOW_SYSTEM.name
            try {
                ThemeMode.valueOf(modeName)
            } catch (e: Exception) {
                ThemeMode.FOLLOW_SYSTEM
            }
        }

    val isEyeProtectionEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[EYE_PROTECTION] ?: false
        }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode.name
        }
    }

    suspend fun setEyeProtectionEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[EYE_PROTECTION] = enabled
        }
    }
}
