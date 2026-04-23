package com.openrattle.wanandroid.history

import com.openrattle.base.model.Article
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val repository: HistoryRepository
) {
    operator fun invoke(): Flow<List<Article>> = repository.allHistory
}

class AddHistoryUseCase @Inject constructor(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(article: Article) {
        repository.addHistory(article)
    }
}

class DeleteHistoryUseCase @Inject constructor(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteHistory(id)
    }
}

class ClearHistoryUseCase @Inject constructor(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke() {
        repository.clearHistory()
    }
}
