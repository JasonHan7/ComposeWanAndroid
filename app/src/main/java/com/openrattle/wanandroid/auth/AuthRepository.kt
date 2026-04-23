package com.openrattle.wanandroid.auth

import com.openrattle.base.model.UserCoinInfo
import com.openrattle.base.model.UserInfo
import com.openrattle.core.network.WanService
import com.openrattle.core.utils.PersistentCookieStorage
import com.openrattle.core.utils.SessionManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val service: WanService,
    private val sessionManager: SessionManager,
    private val cookieStorage: PersistentCookieStorage
) {
    val userFlow: Flow<UserInfo?> = sessionManager.userFlow

    suspend fun login(username: String, password: String): Result<UserInfo> {
        return try {
            val response = service.login(username, password)
            val data = response.data
            if (response.isSuccess() && data != null) {
                sessionManager.saveUser(data)
                Result.success(data)
            } else {
                Result.failure(Exception(response.errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, password: String, repassword: String): Result<UserInfo> {
        return try {
            val response = service.register(username, password, repassword)
            val data = response.data
            if (response.isSuccess() && data != null) {
                sessionManager.saveUser(data)
                Result.success(data)
            } else {
                Result.failure(Exception(response.errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserCoinInfo(): Result<UserCoinInfo> {
        return try {
            val response = service.getUserCoinInfo()
            val data = response.data
            if (response.isSuccess() && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val response = service.logout()
            clearLocalSession()
            if (response.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.errorMsg))
            }
        } catch (e: Exception) {
            clearLocalSession()
            Result.failure(e)
        }
    }

    private suspend fun clearLocalSession() {
        sessionManager.clearSession()
        cookieStorage.clear()
    }
}
