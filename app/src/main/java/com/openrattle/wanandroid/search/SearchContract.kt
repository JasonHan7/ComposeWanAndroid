package com.openrattle.wanandroid.search

import com.openrattle.base.model.Article
import com.openrattle.base.model.HotKey

data class SearchState(
    val keyword: String = "",
    val articles: List<Article> = emptyList(),
    val hotKeys: List<HotKey> = emptyList(),
    val history: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isSearchPerformed: Boolean = false,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val error: String? = null
)

sealed class SearchIntent {
    data class UpdateKeyword(val keyword: String) : SearchIntent()
    data object Search : SearchIntent()
    data object LoadMore : SearchIntent()
    data object LoadHotKeys : SearchIntent()
    data object ClearSearch : SearchIntent()
    data object ClearHistory : SearchIntent()
    data class DeleteHistory(val keyword: String) : SearchIntent()
    data class ToggleCollect(val article: Article) : SearchIntent()
    data class SaveHistory(val article: Article) : SearchIntent()
}

sealed class SearchEffect {
    data class ShowMessage(val message: String) : SearchEffect()
    data object NavigateToLogin : SearchEffect()
}
