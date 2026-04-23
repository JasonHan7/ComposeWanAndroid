package com.openrattle.wanandroid.plaza

import com.openrattle.base.model.Article

data class PlazaState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val error: String? = null,
    val isSharing: Boolean = false
)

sealed class PlazaIntent {
    data object LoadData : PlazaIntent()
    data object Refresh : PlazaIntent()
    data object LoadMore : PlazaIntent()
    data class ToggleCollect(val article: Article) : PlazaIntent()
    data class ShareArticle(val title: String, val link: String) : PlazaIntent()
    data class SaveHistory(val article: Article) : PlazaIntent()
}

sealed class PlazaEffect {
    data class ShowMessage(val message: String) : PlazaEffect()
    data object NavigateToLogin : PlazaEffect()
    data object NavigateBack : PlazaEffect()
}
