package com.openrattle.wanandroid.collect

import com.openrattle.base.model.Article
import com.openrattle.base.utils.UiText

data class CollectState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val error: UiText? = null
)

sealed class CollectIntent {
    data object LoadData : CollectIntent()
    data object Refresh : CollectIntent()
    data object LoadMore : CollectIntent()
    data class Uncollect(val id: Int, val originId: Int) : CollectIntent()
}

sealed class CollectEffect {
    data class ShowMessage(val message: UiText) : CollectEffect()
}
