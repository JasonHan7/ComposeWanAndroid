package com.openrattle.common_ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.openrattle.base.utils.UiText

/**
 * 在 Compose 环境中解析 UiText
 */
@Composable
fun UiText.asString(): String {
    return when (this) {
        is UiText.DynamicString -> value
        is UiText.ResourceString -> stringResource(resId, *args)
    }
}
