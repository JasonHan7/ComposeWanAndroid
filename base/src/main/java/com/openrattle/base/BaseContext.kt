package com.openrattle.base

import android.app.Application
import android.content.Context

/**
 * Base 模块全局 Context 提供者
 * 需在 Application.onCreate() 中初始化
 */
object BaseContext {
    private lateinit var application: Application

    fun init(app: Application) {
        application = app
    }

    fun get(): Context {
        if (!::application.isInitialized) {
            throw IllegalStateException("BaseContext not initialized. Call BaseContext.init(app) in Application.onCreate()")
        }
        return application
    }

    fun isInitialized(): Boolean = ::application.isInitialized
}

/**
 * 获取字符串资源
 */
internal fun getString(resId: Int, vararg formatArgs: Any): String {
    return if (formatArgs.isEmpty()) {
        BaseContext.get().getString(resId)
    } else {
        BaseContext.get().getString(resId, *formatArgs)
    }
}
