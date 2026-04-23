package com.openrattle.wanandroid.search

import androidx.lifecycle.viewModelScope
import com.openrattle.base.model.Article
import com.openrattle.wanandroid.collect.CollectArticleUseCase
import com.openrattle.wanandroid.history.AddHistoryUseCase
import com.openrattle.core.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val collectArticleUseCase: CollectArticleUseCase,
    private val addHistoryUseCase: AddHistoryUseCase
) : MviViewModel<SearchState, SearchIntent, SearchEffect>() {

    init {
        if (state.value.hotKeys.isEmpty()) {
            dispatch(SearchIntent.LoadHotKeys)
        }

        viewModelScope.launch {
            searchUseCase.searchHistory.collectLatest { history ->
                updateState { it.copy(history = history) }
            }
        }
    }

    override fun initialState(): SearchState = SearchState()

    override suspend fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.UpdateKeyword -> {
                if (intent.keyword.isBlank()) {
                    updateState { it.copy(keyword = "", articles = emptyList(), isSearchPerformed = false) }
                } else {
                    updateState { it.copy(keyword = intent.keyword) }
                }
            }
            is SearchIntent.Search -> performSearch()
            is SearchIntent.LoadMore -> loadMore()
            is SearchIntent.LoadHotKeys -> fetchHotKeys()
            is SearchIntent.ClearSearch -> {
                updateState { it.copy(keyword = "", articles = emptyList(), error = null, isSearchPerformed = false) }
            }
            is SearchIntent.ClearHistory -> {
                searchUseCase.clearHistory()
            }
            is SearchIntent.DeleteHistory -> {
                searchUseCase.removeHistory(intent.keyword)
            }
            is SearchIntent.ToggleCollect -> toggleCollect(intent.article)
            is SearchIntent.SaveHistory -> saveHistory(intent.article)
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
            // 更新 UI 状态（搜索结果没有数据库流支持，需手动更新列表状态）
            val newList = state.value.articles.map { 
                if (it.id == article.id) it.copy(collect = !article.collect) else it
            }
            updateState { it.copy(articles = newList) }
            emitEffect(SearchEffect.ShowMessage(if (article.collect) "取消成功" else "收藏成功"))
        }.onFailure { e ->
            emitEffect(SearchEffect.ShowMessage(e.message ?: "操作失败"))
        }
    }

    private suspend fun fetchHotKeys() {
        searchUseCase.getHotKeys()
            .onSuccess { hotKeys ->
                updateState { it.copy(hotKeys = hotKeys) }
            }
    }

    private suspend fun performSearch() {
        val keyword = state.value.keyword
        if (keyword.isBlank()) return

        updateState { it.copy(
            isLoading = true,
            error = null,
            articles = emptyList(),
            currentPage = 0,
            hasMore = true,
            isSearchPerformed = true
        ) }

        searchUseCase(0, keyword)
            .onSuccess { response ->
                updateState { it.copy(
                    isLoading = false,
                    articles = response.datas,
                    currentPage = 0,
                    hasMore = !response.over
                ) }
            }
            .onFailure { e ->
                updateState { it.copy(isLoading = false, error = e.message) }
                emitEffect(SearchEffect.ShowMessage(e.message ?: "搜索失败"))
            }
    }

    private suspend fun loadMore() {
        val currentState = state.value
        if (currentState.isLoadingMore || !currentState.hasMore || currentState.isLoading) return

        val nextPage = currentState.currentPage + 1
        updateState { it.copy(isLoadingMore = true) }

        searchUseCase(nextPage, currentState.keyword)
            .onSuccess { response ->
                updateState { it.copy(
                    isLoadingMore = false,
                    articles = currentState.articles + response.datas,
                    currentPage = nextPage,
                    hasMore = !response.over
                ) }
            }
            .onFailure { e ->
                updateState { it.copy(isLoadingMore = false, error = e.message) }
                emitEffect(SearchEffect.ShowMessage(e.message ?: "加载失败"))
            }
    }
}
