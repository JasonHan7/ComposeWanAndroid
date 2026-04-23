package com.openrattle.wanandroid

import android.app.Application
import com.openrattle.base.BaseContext
import com.openrattle.base.utils.LogUtil
import com.openrattle.core.utils.WebViewManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WanApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 1. 初始化日志开关（确保使用 App 模块的 BuildConfig）
        LogUtil.isLogEnabled = BuildConfig.DEBUG
        // Debug 模式下默认开启全量网络日志，Release 默认关闭
        LogUtil.setKtorLogLevel(if (BuildConfig.DEBUG) LogUtil.KtorLogLevel.ALL else LogUtil.KtorLogLevel.NONE)
        
        // 2. 初始化 Base 模块 Context
        BaseContext.init(this)
        
        // 预热 WebView，提升首屏渲染速度
        WebViewManager.preload(this)
    }
}
