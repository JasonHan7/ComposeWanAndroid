package com.openrattle.core.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.openrattle.base.model.UserInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session_store")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val userKey = stringPreferencesKey("user_info")

    val userFlow: Flow<UserInfo?> = context.sessionDataStore.data.map { preferences ->
        preferences[userKey]?.let {
            try {
                json.decodeFromString<UserInfo>(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun saveUser(user: UserInfo) {
        val jsonString = json.encodeToString(user)
        context.sessionDataStore.edit { preferences ->
            preferences[userKey] = jsonString
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { it.remove(userKey) }
    }
}
