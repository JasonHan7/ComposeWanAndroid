package com.openrattle.core.network

import com.openrattle.base.ApiException
import com.openrattle.base.AppException
import com.openrattle.base.UnauthorizedException
import kotlinx.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * 统一网络请求处理器
 * 
 * 职责：
 * 1. 统一捕获网络异常
 * 2. 将技术异常转换为业务异常
 * 3. 统一线程调度
 */
object NetworkHandler {

    /**
     * 执行网络请求并返回统一格式的Result
     */
    suspend fun <T> execute(block: suspend () -> WanResponse<T>): Result<T?> {
        return try {
            withContext(Dispatchers.IO) {
                val response = block()
                when (response.errorCode) {
                    0 -> Result.success(response.data)
                    -1001 -> Result.failure(AppException.Api.Unauthorized)
                    else -> Result.failure(
                        AppException.Api.Error(response.errorMsg, response.errorCode)
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(e.toNetworkException())
        }
    }

    /**
     * 执行网络请求，返回列表（空数据转为空列表）
     */
    suspend fun <T> executeList(block: suspend () -> WanResponse<List<T>>): Result<List<T>> {
        return try {
            withContext(Dispatchers.IO) {
                val response = block()
                when (response.errorCode) {
                    0 -> Result.success(response.data ?: emptyList())
                    -1001 -> Result.failure(AppException.Api.Unauthorized)
                    else -> Result.failure(
                        AppException.Api.Error(response.errorMsg, response.errorCode)
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(e.toNetworkException())
        }
    }

    /**
     * 执行网络请求，非空断言（分页等场景）
     */
    suspend fun <T> executeNonNull(block: suspend () -> WanResponse<T>): Result<T> {
        return try {
            withContext(Dispatchers.IO) {
                val response = block()
                when {
                    response.errorCode == -1001 -> 
                        Result.failure(AppException.Api.Unauthorized)
                    response.errorCode != 0 -> 
                        Result.failure(AppException.Api.Error(response.errorMsg, response.errorCode))
                    response.data == null -> 
                        Result.failure(AppException.Data.Empty)
                    else -> 
                        Result.success(response.data)
                }
            }
        } catch (e: Exception) {
            Result.failure(e.toNetworkException())
        }
    }
}

/**
 * 简化的顶层函数
 */
suspend fun <T> handleNetwork(block: suspend () -> WanResponse<T>): Result<T?> =
    NetworkHandler.execute(block)

suspend fun <T> handleNetworkList(block: suspend () -> WanResponse<List<T>>): Result<List<T>> =
    NetworkHandler.executeList(block)

suspend fun <T> handleNetworkNonNull(block: suspend () -> WanResponse<T>): Result<T> =
    NetworkHandler.executeNonNull(block)

/**
 * 将网络相关异常转换为 AppException
 */
private fun Exception.toNetworkException(): AppException {
    return when (this) {
        is AppException -> this
        is UnauthorizedException -> AppException.Api.Unauthorized
        is ApiException -> AppException.Api.Error(this.message ?: "", this.code)
        is UnknownHostException, 
        is ConnectException -> 
            AppException.Network.NoConnection(this)
        is SocketTimeoutException, 
        is TimeoutException -> 
            AppException.Network.Timeout(this)
        is IOException -> 
            AppException.Network.NoConnection(this)
        else -> 
            AppException.Network.Unknown(this)
    }
}
