package com.openrattle.base.utils

import android.util.Log
import com.openrattle.base.BuildConfig

/**
 * 全局日志工具类
 *
 * 特性：
 * 1. 自动根据 BuildConfig.DEBUG 控制日志输出
 * 2. 支持全局开关
 * 3. 支持按模块过滤
 * 4. 支持 Ktor 网络日志级别控制（ALL/HEADERS/BODY/INFO/NONE）
 */
object LogUtil {

    // ==================== 基础配置 ====================

    // 全局开关（Debug 默认开启，Release 默认关闭）
    var isLogEnabled: Boolean = BuildConfig.DEBUG

    // 模块级别开关
    private val moduleSwitches = mutableMapOf<String, Boolean>()

    /**
     * 设置模块日志开关
     */
    fun setModuleEnabled(module: String, enabled: Boolean) {
        moduleSwitches[module] = enabled
    }

    /**
     * 检查是否允许输出日志
     */
    private fun shouldLog(tag: String): Boolean {
        return moduleSwitches.getOrDefault(tag, isLogEnabled)
    }

    // ==================== 日志输出方法 ====================

    @JvmStatic
    fun d(tag: String, message: String) {
        if (shouldLog(tag)) Log.d(tag, message)
    }

    @JvmStatic
    fun d(tag: String, message: String, tr: Throwable) {
        if (shouldLog(tag)) Log.d(tag, message, tr)
    }

    @JvmStatic
    fun i(tag: String, message: String) {
        if (shouldLog(tag)) Log.i(tag, message)
    }

    @JvmStatic
    fun i(tag: String, message: String, tr: Throwable) {
        if (shouldLog(tag)) Log.i(tag, message, tr)
    }

    @JvmStatic
    fun w(tag: String, message: String) {
        if (shouldLog(tag)) Log.w(tag, message)
    }

    @JvmStatic
    fun w(tag: String, message: String, tr: Throwable) {
        if (shouldLog(tag)) Log.w(tag, message, tr)
    }

    @JvmStatic
    fun e(tag: String, message: String) {
        if (shouldLog(tag)) Log.e(tag, message)
    }

    @JvmStatic
    fun e(tag: String, message: String, tr: Throwable) {
        if (shouldLog(tag)) Log.e(tag, message, tr)
    }

    // ==================== Ktor 网络日志配置 ====================

    /**
     * Ktor 网络日志级别
     *
     * - ALL: 所有日志（请求行、响应行、请求头、响应头、请求体、响应体）
     * - HEADERS: 仅请求/响应头信息
     * - BODY: 仅请求/响应体内容
     * - INFO: 仅基本信息（请求方法、URL、响应状态码）
     * - NONE: 关闭网络日志
     */
    enum class KtorLogLevel {
        ALL,      // 所有日志
        HEADERS,  // 仅头信息
        BODY,     // 仅 body
        INFO,     // 仅基本信息
        NONE      // 关闭
    }

    /**
     * 当前 Ktor 网络日志级别（默认为 ALL if DEBUG else NONE）
     *
     * 注意：此值在 HttpClient 创建时读取，修改后需重启应用生效
     */
    var ktorLogLevel: KtorLogLevel = if (BuildConfig.DEBUG) KtorLogLevel.ALL else KtorLogLevel.NONE
        private set

    /**
     * 设置 Ktor 网络日志级别
     *
     * @param level 日志级别 [KtorLogLevel.ALL] / [KtorLogLevel.HEADERS] / [KtorLogLevel.BODY] / [KtorLogLevel.INFO] / [KtorLogLevel.NONE]
     *
     * 示例：
     * ```kotlin
     * // 查看所有网络日志（最详细）
     * LogUtil.setKtorLogLevel(LogUtil.KtorLogLevel.ALL)
     *
     * // 仅查看请求/响应头
     * LogUtil.setKtorLogLevel(LogUtil.KtorLogLevel.HEADERS)
     *
     * // 仅查看请求/响应体
     * LogUtil.setKtorLogLevel(LogUtil.KtorLogLevel.BODY)
     *
     * // 关闭网络日志
     * LogUtil.setKtorLogLevel(LogUtil.KtorLogLevel.NONE)
     * ```
     */
    fun setKtorLogLevel(level: KtorLogLevel) {
        ktorLogLevel = level
    }

    /**
     * 设置 Ktor 网络日志级别（字符串方式，方便动态配置）
     *
     * @param level 字符串："all" | "headers" | "body" | "info" | "none"（不区分大小写）
     * @return 是否设置成功（无效字符串返回 false）
     *
     * 示例：
     * ```kotlin
     * // 从远程配置读取日志级别
     * LogUtil.setKtorLogLevel(remoteConfig.getString("network_log_level"))
     * ```
     */
    fun setKtorLogLevel(level: String): Boolean {
        ktorLogLevel = when (level.lowercase()) {
            "all" -> KtorLogLevel.ALL
            "headers", "head" -> KtorLogLevel.HEADERS
            "body" -> KtorLogLevel.BODY
            "info" -> KtorLogLevel.INFO
            "none", "off" -> KtorLogLevel.NONE
            else -> return false
        }
        return true
    }

    // ==================== 便捷扩展 ====================

    /**
     * 打印方法调用栈（调试用）
     */
    @JvmStatic
    fun printStackTrace(tag: String) {
        if (shouldLog(tag)) {
            Thread.currentThread().stackTrace.drop(3).take(5).forEach {
                Log.d(tag, "    at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})")
            }
        }
    }
}

/**
 * 日志扩展函数（可选使用）
 */
fun Any.logd(tag: String, message: String) = LogUtil.d(tag, message)
fun Any.logi(tag: String, message: String) = LogUtil.i(tag, message)
fun Any.logw(tag: String, message: String) = LogUtil.w(tag, message)
fun Any.loge(tag: String, message: String) = LogUtil.e(tag, message)
