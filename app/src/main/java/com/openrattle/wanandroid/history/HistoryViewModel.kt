package com.openrattle.wanandroid.history

import androidx.lifecycle.viewModelScope
import com.openrattle.base.model.Article
import com.openrattle.wanandroid.collect.CollectArticleUseCase
import com.openrattle.core.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val deleteHistoryUseCase: DeleteHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase,
    private val collectArticleUseCase: CollectArticleUseCase
) : MviViewModel<HistoryState, HistoryIntent, HistoryEffect>() {

    override fun initialState(): HistoryState = HistoryState()

    init {
        viewModelScope.launch {
            getHistoryUseCase().collectLatest { articles ->
                updateState { it.copy(articles = articles) }
            }
        }
    }

    override suspend fun handleIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.LoadData -> {} // Initial data loaded via flow in init
            is HistoryIntent.DeleteHistory -> deleteHistoryUseCase(intent.id)
            is HistoryIntent.ClearAll -> clearHistoryUseCase()
            is HistoryIntent.ToggleCollect -> toggleCollect(intent.article)
        }
    }

    private suspend fun toggleCollect(article: Article) {
        val result = if (article.collect) {
            collectArticleUseCase.uncollectFromArticle(article.id)
        } else {
            collectArticleUseCase.collect(article.id)
        }
        
        result.onSuccess {
            emitEffect(HistoryEffect.ShowMessage(if (article.collect) "取消成功" else "收藏成功"))
        }.onFailure { e ->
            emitEffect(HistoryEffect.ShowMessage(e.message ?: "操作失败"))
        }
    }
}
