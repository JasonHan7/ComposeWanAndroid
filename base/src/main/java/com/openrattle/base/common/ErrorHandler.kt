package com.openrattle.base.common

import com.openrattle.base.AppException
import com.openrattle.base.toAppException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 统一错误处理器
 * 
 * 职责：
 * 1. 提供全局异常流，供 UI 或日志系统订阅
 * 2. 提供便捷方法将 Throwable 转换为 AppException 并分发
 * 3. 封装常见的重试和业务错误判断逻辑
 */
class ErrorHandler {
    
    private val _errorFlow = MutableSharedFlow<AppException>(extraBufferCapacity = 1)
    val errorFlow: SharedFlow<AppException> = _errorFlow.asSharedFlow()
    
    fun handle(
        throwable: Throwable,
        fallback: (() -> Unit)? = null
    ): AppException {
        val appException = throwable.toAppException()
        _errorFlow.tryEmit(appException)
        fallback?.invoke()
        return appException
    }
    
    fun <T> handleResult(
        result: Result<T>,
        onError: ((AppException) -> Unit)? = null
    ): Result<T> {
        result.onFailure { throwable ->
            val appException = handle(throwable)
            onError?.invoke(appException)
        }
        return result
    }
    
    fun shouldRetry(exception: AppException): Boolean {
        return when (exception) {
            is AppException.Network.Timeout,
            is AppException.Network.ServerError,
            is AppException.Network.NoConnection -> true
            else -> false
        }
    }
    
    fun isUnauthorized(exception: AppException): Boolean {
        return exception is AppException.Api.Unauthorized
    }
}

interface ErrorCollector {
    fun collect(error: AppException)
}
