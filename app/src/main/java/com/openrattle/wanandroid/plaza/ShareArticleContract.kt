package com.openrattle.wanandroid.plaza

data class ShareArticleState(
    val isSharing: Boolean = false,
    val error: String? = null
)

sealed class ShareArticleIntent {
    data class ShareArticle(val title: String, val link: String) : ShareArticleIntent()
}

sealed class ShareArticleEffect {
    data class ShowMessage(val message: String) : ShareArticleEffect()
    data object NavigateToLogin : ShareArticleEffect()
    data object NavigateBack : ShareArticleEffect()
}
