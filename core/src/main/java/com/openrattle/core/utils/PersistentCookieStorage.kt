package com.openrattle.core.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cookies_store")

class PersistentCookieStorage(private val context: Context) : CookiesStorage {
    private val json = Json { ignoreUnknownKeys = true }
    private val cookiesKey = stringPreferencesKey("persistent_cookies")
    
    private val container = ConcurrentHashMap<String, MutableList<Cookie>>()
    private val isLoaded = AtomicBoolean(false)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private suspend fun ensureLoaded() {
        if (isLoaded.compareAndSet(false, true)) {
            loadFromDisk()
        }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        ensureLoaded()
        val host = requestUrl.host
        val currentCookies = container.getOrPut(host) { mutableListOf() }
        currentCookies.removeAll { it.name == cookie.name }
        currentCookies.add(cookie)
        
        // 异步保存，不阻塞网络请求
        scope.launch {
            saveToDisk()
        }
    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        ensureLoaded()
        return container[requestUrl.host] ?: emptyList()
    }

    private suspend fun saveToDisk() {
        val serializableMap = container.mapValues { entry ->
            entry.value.map { it.toSerializable() }
        }
        val jsonString = json.encodeToString(serializableMap)
        context.dataStore.edit { preferences ->
            preferences[cookiesKey] = jsonString
        }
    }

    private suspend fun loadFromDisk() {
        val jsonString = context.dataStore.data.map { preferences ->
            preferences[cookiesKey]
        }.first()

        if (!jsonString.isNullOrBlank()) {
            try {
                val map: Map<String, List<SerializableCookie>> = json.decodeFromString(jsonString)
                map.forEach { (host, cookies) ->
                    container[host] = cookies.map { it.toCookie() }.toMutableList()
                }
            } catch (e: Exception) {
                // Ignore parse errors
            }
        }
    }

    suspend fun clear() {
        container.clear()
        context.dataStore.edit { it.remove(cookiesKey) }
    }

    override fun close() {
        container.clear()
        scope.cancel()
    }
}

@Serializable
data class SerializableCookie(
    val name: String,
    val value: String,
    val domain: String? = null,
    val path: String? = null,
    val expires: Long? = null,
    val secure: Boolean = false,
    val httpOnly: Boolean = false
)

fun Cookie.toSerializable() = SerializableCookie(
    name = name,
    value = value,
    domain = domain,
    path = path,
    expires = expires?.timestamp,
    secure = secure,
    httpOnly = httpOnly
)

fun SerializableCookie.toCookie() = Cookie(
    name = name,
    value = value,
    domain = domain,
    path = path,
    expires = expires?.let { io.ktor.util.date.GMTDate(it) },
    secure = secure,
    httpOnly = httpOnly
)
