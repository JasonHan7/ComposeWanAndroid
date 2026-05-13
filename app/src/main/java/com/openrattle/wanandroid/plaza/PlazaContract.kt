package com.openrattle.wanandroid.plaza

import com.openrattle.base.model.Article
import com.openrattle.base.utils.UiText

data class PlazaState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val error: UiText? = null
)

sealed class PlazaIntent {
    data object LoadData : PlazaIntent()
    data object Refresh : PlazaIntent()
    data object LoadMore : PlazaIntent()
    data class ToggleCollect(val article: Article) : PlazaIntent()
    data class SaveHistory(val article: Article) : PlazaIntent()
}

sealed class PlazaEffect {
    data class ShowMessage(val message: UiText) : PlazaEffect()
    data object NavigateToLogin : PlazaEffect()
}
