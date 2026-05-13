package com.openrattle.wanandroid.history

import com.openrattle.wanandroid.R
import androidx.lifecycle.viewModelScope
import com.openrattle.base.onError
import com.openrattle.base.model.Article
import com.openrattle.base.utils.UiText
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
            emitEffect(HistoryEffect.ShowMessage(
                if (article.collect) UiText.ResourceString(R.string.uncollect_success) 
                else UiText.ResourceString(R.string.collect_success)
            ))
        }.onError { e ->
            emitEffect(HistoryEffect.ShowMessage(UiText.DynamicString(e.message)))
        }
    }
}
