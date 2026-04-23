package com.openrattle.core.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.searchHistoryDataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history_store")

@Singleton
class SearchHistoryManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val historyKey = stringPreferencesKey("search_history")

    val historyFlow: Flow<List<String>> = context.searchHistoryDataStore.data.map { preferences ->
        preferences[historyKey]?.let {
            try {
                json.decodeFromString<List<String>>(it)
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }

    suspend fun addSearchHistory(keyword: String) {
        if (keyword.isBlank()) return
        
        context.searchHistoryDataStore.edit { preferences ->
            val currentHistory = preferences[historyKey]?.let {
                try {
                    json.decodeFromString<List<String>>(it)
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()

            val newHistory = (listOf(keyword) + currentHistory.filter { it != keyword })
            
            preferences[historyKey] = json.encodeToString(newHistory)
        }
    }

    suspend fun clearHistory() {
        context.searchHistoryDataStore.edit { it.remove(historyKey) }
    }

    suspend fun removeHistory(keyword: String) {
        context.searchHistoryDataStore.edit { preferences ->
            val currentHistory = preferences[historyKey]?.let {
                try {
                    json.decodeFromString<List<String>>(it)
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()
            
            val newHistory = currentHistory.filter { it != keyword }
            preferences[historyKey] = json.encodeToString(newHistory)
        }
    }
}
