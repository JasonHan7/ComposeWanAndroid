package com.openrattle.base.utils

import android.content.Context
import androidx.annotation.StringRes

/**
 * 一个用于在 ViewModel 中持有字符串或字符串资源的包装类
 * 
 * 解决了 ViewModel 无法直接获取 Context 的问题，同时保持了代码的可测试性
 */
sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class ResourceString(
        @param:StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is ResourceString -> context.getString(resId, *args)
        }
    }
}
