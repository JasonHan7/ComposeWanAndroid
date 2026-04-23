package com.openrattle.base

import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * API 异常（用于网络响应转换）
 */
class ApiException(val code: Int, message: String) : Exception(message)

/**
 * 未登录异常
 */
class UnauthorizedException(message: String) : Exception(message)

/**
 * 应用异常类型
 * 统一分类所有可能的错误
 */
sealed class AppException(
    override val message: String, 
    override val cause: Throwable? = null
) : Exception(message, cause) {
    
    /**
     * 网络相关错误
     */
    sealed class Network(
        message: String, 
        cause: Throwable? = null
    ) : AppException(message, cause) {
        
        /**
         * 无网络连接
         */
        data class NoConnection(val originalCause: Throwable? = null) : 
            Network("网络连接失败，请检查网络设置", originalCause)
        
        /**
         * 连接超时
         */
        data class Timeout(val originalCause: Throwable? = null) : 
            Network("连接超时，请稍后重试", originalCause)
        
        /**
         * 服务器错误 (5xx)
         */
        data class ServerError(val statusCode: Int, val originalCause: Throwable? = null) : 
            Network("服务器繁忙，请稍后重试 ($statusCode)", originalCause)
        
        /**
         * 其他网络错误
         */
        data class Unknown(val originalCause: Throwable? = null) : 
            Network("网络请求失败", originalCause)
    }
    
    /**
     * API业务错误
     */
    sealed class Api(
        message: String, 
        open val errorCode: Int = -1
    ) : AppException(message) {
        /**
         * 未登录
         */
        data object Unauthorized : Api("请先登录", -1001)
        
        /**
         * 参数错误
         */
        data class BadRequest(override val message: String) : Api(message, -1002)
        
        /**
         * 其他API错误
         */
        data class Error(override val message: String, override val errorCode: Int) : 
            Api(message, errorCode)
    }
    
    /**
     * 数据错误
     */
    sealed class Data(message: String) : AppException(message) {
        /**
         * 数据为空（当业务上不允许为空时）
         */
        data object Empty : Data("暂无数据")
        
        /**
         * 数据解析错误
         */
        data class ParseError(val originalCause: Throwable? = null) : 
            Data("数据解析失败")
    }
    
    /**
     * 本地错误
     */
    sealed class Local(message: String) : AppException(message) {
        /**
         * 数据库错误
         */
        data class Database(override val message: String = "数据库操作失败") : Local(message)
        
        /**
         * 缓存错误
         */
        data class Cache(override val message: String = "缓存操作失败") : Local(message)
    }
}

/**
 * 将 Throwable 转换为 AppException
 */
fun Throwable.toAppException(): AppException {
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

/**
 * 获取用户友好的错误信息
 */
fun AppException.getUserFriendlyMessage(): String = message

/**
 * Result 扩展函数
 * 提供更方便的错误处理方式
 */

/**
 * 成功时执行，返回原始Result
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (isSuccess) {
        action(getOrNull()!!)
    }
    return this
}

/**
 * 失败时执行，返回原始Result
 */
inline fun <T> Result<T>.onError(action: (AppException) -> Unit): Result<T> {
    if (isFailure) {
        val exception = exceptionOrNull()?.toAppException() 
            ?: AppException.Network.Unknown()
        action(exception)
    }
    return this
}

/**
 * 失败时执行（使用原始Throwable）
 */
inline fun <T> Result<T>.onFailure(action: (Throwable) -> Unit): Result<T> {
    if (isFailure) {
        action(exceptionOrNull()!!)
    }
    return this
}

/**
 * 获取错误信息（用户友好）
 */
fun <T> Result<T>.errorMessage(): String {
    return exceptionOrNull()?.toAppException()?.getUserFriendlyMessage()
        ?: "操作失败"
}

/**
 * 获取AppException
 */
fun <T> Result<T>.appException(): AppException? {
    return exceptionOrNull()?.toAppException()
}

/**
 * 是否需要重试
 */
fun <T> Result<T>.shouldRetry(): Boolean {
    val exception = exceptionOrNull()?.toAppException() ?: return false
    return when (exception) {
        is AppException.Network.Timeout,
        is AppException.Network.ServerError,
        is AppException.Network.NoConnection -> true
        else -> false
    }
}

/**
 * 是否是未登录错误
 */
fun <T> Result<T>.isUnauthorized(): Boolean {
    return exceptionOrNull()?.toAppException() is AppException.Api.Unauthorized
}
