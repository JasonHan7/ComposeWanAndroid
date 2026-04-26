package com.openrattle.wanandroid.qa

import com.openrattle.base.AppException
import com.openrattle.base.onError
import com.openrattle.base.model.Article
import androidx.lifecycle.viewModelScope
import com.openrattle.base.utils.LogUtil
import com.openrattle.wanandroid.collect.CollectArticleUseCase
import com.openrattle.wanandroid.history.AddHistoryUseCase
import com.openrattle.core.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "QaVM"

@HiltViewModel
class QaViewModel @Inject constructor(
    private val getQaListUseCase: GetQaListUseCase,
    private val loadMoreQaArticlesUseCase: LoadMoreQaArticlesUseCase,
    private val collectArticleUseCase: CollectArticleUseCase,
    private val addHistoryUseCase: AddHistoryUseCase
) : MviViewModel<QaState, QaIntent, QaEffect>() {

    override fun initialState(): QaState = QaState()

    init {
        viewModelScope.launch {
            getQaListUseCase.cachedArticles.collectLatest { articles ->
                updateState { it.copy(articles = articles) }
            }
        }
        dispatch(QaIntent.LoadData)
    }

    override suspend fun handleIntent(intent: QaIntent) {
        when (intent) {
            is QaIntent.LoadData -> loadData()
            is QaIntent.Refresh -> refresh()
            is QaIntent.LoadMore -> loadMore()
            is QaIntent.ToggleCollect -> toggleCollect(intent.article)
            is QaIntent.SaveHistory -> saveHistory(intent.article)
        }
    }

    private suspend fun saveHistory(article: Article) {
        addHistoryUseCase(article)
    }

    private suspend fun toggleCollect(article: Article) {
        val result = if (article.collect) {
            collectArticleUseCase.uncollectFromArticle(article.id)
        } else {
            collectArticleUseCase.collect(article.id)
        }
        
        result.onSuccess {
            emitEffect(QaEffect.ShowMessage(if (article.collect) "取消成功" else "收藏成功"))
        }.onError { e ->
            handleException(e)
        }
    }

    /**
     * 统一异常处理
     */
    private fun handleException(exception: AppException) {
        when (exception) {
            is AppException.Api.Unauthorized -> {
                emitEffect(QaEffect.ShowMessage("请先登录"))
                emitEffect(QaEffect.NavigateToLogin)
            }
            else -> {
                emitEffect(QaEffect.ShowMessage(exception.message))
            }
        }
    }

    private suspend fun loadData() {
        if (state.value.isLoading && state.value.articles.isNotEmpty()) return
        updateState { it.copy(isLoading = true, error = null) }

        getQaListUseCase(forceRefresh = false)
            .onSuccess { isOver ->
                updateState { it.copy(isLoading = false, hasMore = !isOver) }
                // 后台静默刷新
                viewModelScope.launch {
                    getQaListUseCase.refresh()
                }
            }
            .onError { e ->
                updateState { it.copy(isLoading = false, error = e.message) }
            }
    }

    private suspend fun refresh() {
        if (state.value.isLoading) return
        updateState { it.copy(isLoading = true, error = null) }

        getQaListUseCase(forceRefresh = true)
            .onSuccess { isOver ->
                updateState {
                    it.copy(
                        isLoading = false,
                        currentPage = 0,
                        hasMore = !isOver
                    )
                }
            }
            .onError { e ->
                updateState { it.copy(isLoading = false, error = e.message) }
                emitEffect(QaEffect.ShowMessage(e.message))
            }
    }

    private suspend fun loadMore() {
        val currentState = state.value
        if (currentState.isLoadingMore || !currentState.hasMore || currentState.isLoading) return

        // 计算下一页：取当前列表中的最大页码 + 1
        val maxPage = currentState.articles.maxOfOrNull { it.page } ?: -1
        val nextPage = maxPage + 1

        LogUtil.d(TAG, "⬇️ 问答加载更多: nextPage=$nextPage, currentSize=${currentState.articles.size}")
        updateState { it.copy(isLoadingMore = true) }

        loadMoreQaArticlesUseCase(nextPage)
            .onSuccess { (_, isOver) ->
                updateState {
                    it.copy(
                        isLoadingMore = false,
                        hasMore = !isOver
                    )
                }
            }
            .onError { e ->
                updateState { it.copy(isLoadingMore = false) }
                emitEffect(QaEffect.ShowMessage(e.message))
            }
    }
}
