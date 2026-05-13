package com.openrattle.wanandroid.history

import com.openrattle.base.model.Article
import com.openrattle.base.utils.UiText

data class HistoryState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false
)

sealed class HistoryIntent {
    data object LoadData : HistoryIntent()
    data class DeleteHistory(val id: Int) : HistoryIntent()
    data object ClearAll : HistoryIntent()
    data class ToggleCollect(val article: Article) : HistoryIntent()
}

sealed class HistoryEffect {
    data class ShowMessage(val message: UiText) : HistoryEffect()
    data object NavigateToLogin : HistoryEffect()
}
