package com.openrattle.wanandroid.plaza

import com.openrattle.base.utils.UiText

data class ShareArticleState(
    val isSharing: Boolean = false,
    val error: UiText? = null
)

sealed class ShareArticleIntent {
    data class ShareArticle(val title: String, val link: String) : ShareArticleIntent()
}

sealed class ShareArticleEffect {
    data class ShowMessage(val message: UiText) : ShareArticleEffect()
    data object NavigateToLogin : ShareArticleEffect()
    data object NavigateBack : ShareArticleEffect()
}
