package com.openrattle.wanandroid.home

import com.openrattle.base.model.Article
import com.openrattle.base.model.Banner

/**
 * Home页面UI状态
 */
data class HomeState(
    val articles: List<Article> = emptyList(),
    val banners: List<Banner> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val unreadCount: Int = 0,
    val error: String? = null
)

/**
 * Home页面用户意图
 */
sealed class HomeIntent {
    data object LoadData : HomeIntent()
    data object Refresh : HomeIntent()
    data object LoadMore : HomeIntent()
    data class ToggleCollect(val article: Article) : HomeIntent()
    data class SaveHistory(val article: Article) : HomeIntent()
}

/**
 * Home页面副作用
 */
sealed class HomeEffect {
    data class ShowMessage(val message: String) : HomeEffect()
    data object NavigateToLogin : HomeEffect()
}
