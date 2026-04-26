package com.openrattle.wanandroid.collect

import com.openrattle.base.onError
import com.openrattle.core.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CollectViewModel @Inject constructor(
    private val collectArticleUseCase: CollectArticleUseCase
) : MviViewModel<CollectState, CollectIntent, CollectEffect>() {

    init {
        dispatch(CollectIntent.LoadData)
    }

    override fun initialState(): CollectState = CollectState()

    override suspend fun handleIntent(intent: CollectIntent) {
        when (intent) {
            is CollectIntent.LoadData -> loadData()
            is CollectIntent.Refresh -> refresh()
            is CollectIntent.LoadMore -> loadMore()
            is CollectIntent.Uncollect -> uncollect(intent.id, intent.originId)
        }
    }

    private suspend fun loadData() {
        if (state.value.isLoading) return
        updateState { it.copy(isLoading = true, error = null) }

        collectArticleUseCase.getCollectList(0)
            .onSuccess { response ->
                updateState { it.copy(
                    isLoading = false,
                    articles = response.datas,
                    currentPage = 0,
                    hasMore = !response.over
                ) }
            }
            .onError { e ->
                updateState { it.copy(isLoading = false, error = e.message) }
                emitEffect(CollectEffect.ShowMessage(e.message))
            }
    }

    private suspend fun refresh() {
        if (state.value.isLoading) return
        updateState { it.copy(isLoading = true, error = null) }

        collectArticleUseCase.getCollectList(0)
            .onSuccess { response ->
                updateState { it.copy(
                    isLoading = false,
                    articles = response.datas,
                    currentPage = 0,
                    hasMore = !response.over
                ) }
            }
            .onError { e ->
                updateState { it.copy(isLoading = false, error = e.message) }
                emitEffect(CollectEffect.ShowMessage(e.message))
            }
    }

    private suspend fun loadMore() {
        val currentState = state.value
        if (currentState.isLoadingMore || !currentState.hasMore || currentState.isLoading) return

        val nextPage = currentState.currentPage + 1
        updateState { it.copy(isLoadingMore = true) }

        collectArticleUseCase.getCollectList(nextPage)
            .onSuccess { response ->
                updateState { it.copy(
                    isLoadingMore = false,
                    articles = currentState.articles + response.datas,
                    currentPage = nextPage,
                    hasMore = !response.over
                ) }
            }
            .onError { e ->
                updateState { it.copy(isLoadingMore = false) }
                emitEffect(CollectEffect.ShowMessage(e.message))
            }
    }

    private suspend fun uncollect(id: Int, originId: Int) {
        collectArticleUseCase.uncollectFromList(id, originId)
            .onSuccess {
                val updatedArticles = state.value.articles.filter { it.id != id }
                updateState { it.copy(articles = updatedArticles) }
                emitEffect(CollectEffect.ShowMessage("已取消收藏"))
            }
            .onError { e ->
                emitEffect(CollectEffect.ShowMessage(e.message))
            }
    }
}
