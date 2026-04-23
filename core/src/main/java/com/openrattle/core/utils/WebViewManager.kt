package com.openrattle.core.utils

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import java.util.LinkedList

/**
 * WebView 性能优化管理类
 */
object WebViewManager {
    private const val TAG = "WebViewManager"
    private const val MAX_POOL_SIZE = 2
    private val webViewPool = LinkedList<WebView>()

    /**
     * 在 Application 中预加载
     */
    fun preload(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                // 设置数据目录后缀，防止多进程冲突
                WebView.setDataDirectorySuffix("web_process")
            } catch (e: Exception) {
                Log.e(TAG, "setDataDirectorySuffix error: ${e.message}")
            }
        }

        prepareWebView(context.applicationContext)
    }

    /**
     * 获取一个预先创建好的 WebView，如果没有则新建
     */
    fun acquire(context: Context): WebView {
        return webViewPool.poll() ?: createWebView(context.applicationContext)
    }

    /**
     * 释放 WebView 到池中或销毁
     */
    fun release(webView: WebView) {
        try {
            webView.stopLoading()
            webView.clearHistory()
            webView.removeAllViews()
            (webView.parent as? ViewGroup)?.removeView(webView)
            webView.loadUrl("about:blank")
            
            if (webViewPool.size < MAX_POOL_SIZE) {
                webViewPool.offer(webView)
                Log.d(TAG, "WebView released to pool, size: ${webViewPool.size}")
            } else {
                webView.destroy()
                Log.d(TAG, "WebView destroyed (pool full)")
            }
        } catch (e: Exception) {
            Log.e(TAG, "release error: ${e.message}")
        }
    }

    private fun prepareWebView(context: Context) {
        if (webViewPool.size < MAX_POOL_SIZE) {
            val webView = createWebView(context)
            webViewPool.offer(webView)
            Log.d(TAG, "WebView pre-warmed, pool size: ${webViewPool.size}")
        }
    }

    private fun createWebView(context: Context): WebView {
        return WebView(context).apply {
            // 设置一个空白页加速内核初始化
            loadUrl("about:blank")
        }
    }
}
