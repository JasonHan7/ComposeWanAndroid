package com.openrattle.wanandroid.qa

import com.openrattle.base.model.Article
import com.openrattle.base.utils.UiText

data class QaState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val error: UiText? = null
)

sealed class QaIntent {
    data object LoadData : QaIntent()
    data object Refresh : QaIntent()
    data object LoadMore : QaIntent()
    data class ToggleCollect(val article: Article) : QaIntent()
    data class SaveHistory(val article: Article) : QaIntent()
}

sealed class QaEffect {
    data class ShowMessage(val message: UiText) : QaEffect()
    data object NavigateToLogin : QaEffect()
}
