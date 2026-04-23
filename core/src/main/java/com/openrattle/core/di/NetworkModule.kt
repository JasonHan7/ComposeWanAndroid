package com.openrattle.core.di

import android.content.Context
import com.openrattle.core.network.KtorWanService
import com.openrattle.core.network.WanService
import com.openrattle.base.utils.LogUtil
import com.openrattle.core.utils.PersistentCookieStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.Logger
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideWanService(client: HttpClient): WanService {
        return KtorWanService(client)
    }

    @Provides
    @Singleton
    fun provideCookieStorage(@ApplicationContext context: Context): PersistentCookieStorage {
        return PersistentCookieStorage(context)
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        cookieStorage: PersistentCookieStorage,
        @ApplicationContext context: Context
    ): HttpClient {
        return HttpClient(OkHttp) {
            // HTTP 缓存配置
            install(HttpCache) {
                val cacheDir = File(context.cacheDir, "http_cache")
                publicStorage(FileStorage(cacheDir))
            }
            
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
            install(HttpCookies) {
                storage = cookieStorage
            }
            
            // Ktor 日志配置（根据 LogUtil 控制）
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        LogUtil.d("Ktor", message)
                    }
                }
                level = when (LogUtil.ktorLogLevel) {
                    LogUtil.KtorLogLevel.ALL -> LogLevel.ALL
                    LogUtil.KtorLogLevel.HEADERS -> LogLevel.HEADERS
                    LogUtil.KtorLogLevel.BODY -> LogLevel.BODY
                    LogUtil.KtorLogLevel.INFO -> LogLevel.INFO
                    LogUtil.KtorLogLevel.NONE -> LogLevel.NONE
                }
            }

            engine {
                config {
                    connectTimeout(15, TimeUnit.SECONDS)
                    readTimeout(15, TimeUnit.SECONDS)
                    writeTimeout(15, TimeUnit.SECONDS)
                }
            }
        }
    }
}
