package com.openrattle.wanandroid.search

import com.openrattle.base.model.Article
import com.openrattle.base.model.HotKey
import com.openrattle.base.model.PagingResponse
import com.openrattle.core.utils.SearchHistoryManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repository: SearchRepository,
    private val historyManager: SearchHistoryManager
) {
    val searchHistory: Flow<List<String>> = historyManager.historyFlow

    suspend operator fun invoke(page: Int, keyword: String): Result<PagingResponse<Article>> {
        return repository.search(page, keyword)
            .onSuccess {
                historyManager.addSearchHistory(keyword)
            }
    }

    suspend fun getHotKeys(): Result<List<HotKey>> = repository.getHotKeys()

    suspend fun clearHistory() {
        historyManager.clearHistory()
    }

    suspend fun removeHistory(keyword: String) {
        historyManager.removeHistory(keyword)
    }
}
