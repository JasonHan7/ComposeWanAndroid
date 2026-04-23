package com.openrattle.wanandroid.qa

import com.openrattle.base.model.Article

data class QaState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val error: String? = null
)

sealed class QaIntent {
    data object LoadData : QaIntent()
    data object Refresh : QaIntent()
    data object LoadMore : QaIntent()
    data class ToggleCollect(val article: Article) : QaIntent()
    data class SaveHistory(val article: Article) : QaIntent()
}

sealed class QaEffect {
    data class ShowMessage(val message: String) : QaEffect()
    data object NavigateToLogin : QaEffect()
}
