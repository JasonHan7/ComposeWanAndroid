package com.openrattle.wanandroid.history

import com.openrattle.base.model.Article
import com.openrattle.core.database.local.HistoryLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val local: HistoryLocalDataSource
) {
    val allHistory: Flow<List<Article>> = local.allHistory

    suspend fun addHistory(article: Article) {
        local.addHistory(article)
    }

    suspend fun deleteHistory(id: Int) {
        local.deleteHistory(id)
    }

    suspend fun clearHistory() {
        local.clearHistory()
    }
}
