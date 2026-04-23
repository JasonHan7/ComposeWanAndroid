package com.openrattle.wanandroid.home

import com.openrattle.base.utils.LogUtil
import androidx.lifecycle.viewModelScope
import com.openrattle.base.AppException
import com.openrattle.base.model.Article
import com.openrattle.base.onError
import com.openrattle.base.common.ErrorHandler
import com.openrattle.core.MviViewModel
import com.openrattle.wanandroid.collect.CollectArticleUseCase
import com.openrattle.wanandroid.history.AddHistoryUseCase
import com.openrattle.wanandroid.message.GetUnreadMessageCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeVM"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getArticlesUseCase: GetArticlesUseCase,
    private val getBannersUseCase: GetBannersUseCase,
    private val loadMoreArticlesUseCase: LoadMoreArticlesUseCase,
    private val collectArticleUseCase: CollectArticleUseCase,
    private val addHistoryUseCase: AddHistoryUseCase,
    private val getUnreadMessageCountUseCase: GetUnreadMessageCountUseCase,
    private val errorHandler: ErrorHandler
) : MviViewModel<HomeState, HomeIntent, HomeEffect>() {

    override fun initialState(): HomeState = HomeState()

    init {
        viewModelScope.launch {
            getBannersUseCase.allBanners.collectLatest { banners ->
                LogUtil.d(TAG, "🎡 Banner 更新: size=${banners.size}")
                updateState { it.copy(banners = banners) }
            }
        }
        viewModelScope.launch {
            getArticlesUseCase.allArticles.collectLatest { articles ->
                val pages = articles.map { it.page }.toSet().sorted()
                LogUtil.d(TAG, "📄 文章更新: size=${articles.size}, pages=$pages")
                updateState { it.copy(articles = articles) }
            }
        }
        dispatch(HomeIntent.LoadData)
    }

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadData -> loadData()
            is HomeIntent.Refresh -> refresh()
            is HomeIntent.LoadMore -> loadMore()
            is HomeIntent.ToggleCollect -> toggleCollect(intent.article)
            is HomeIntent.SaveHistory -> saveHistory(intent.article)
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
            emitEffect(HomeEffect.ShowMessage(if (article.collect) "取消成功" else "收藏成功"))
        }.onError { e ->
            handleException(e)
        }
    }

    private suspend fun loadData() {
        // 防止重复加载
        if (state.value.isLoading) return

        updateState { it.copy(isLoading = true, error = null) }

        // 1. 获取 Banner (后台执行)
        viewModelScope.launch {
            try {
                getBannersUseCase()
            } catch (e: Exception) {
                LogUtil.e(TAG, "⚠️ Banner 获取失败", e)
            }
        }

        // 2. 获取未读消息数量 (后台执行)
        viewModelScope.launch {
            getUnreadMessageCountUseCase()
                .onSuccess { count ->
                    updateState { it.copy(unreadCount = count) }
                }
        }

        // 3. 获取文章
        try {
            val articlesResult = getArticlesUseCase(forceRefresh = false)
            // 如果是“命中缓存”返回的成功，我们可以立即结束 Loading 状态（因为 Flow 会推送缓存数据）
            // 并发起一个后台刷新任务
            articlesResult
                .onSuccess {
                    updateState { it.copy(isLoading = false) }
                    // 如果刚才不是强制刷新且有缓存，则发起后台同步
                    // (目前 getArticlesUseCase 的逻辑是同步返回，所以我们在这里手动 launch 一个异步刷新)
                    viewModelScope.launch {
                        getArticlesUseCase.refresh()
                    }
                }
                .onError { exception ->
                    handleException(exception)
                    updateState { it.copy(isLoading = false, error = exception.message) }
                }
        } catch (e: Exception) {
            LogUtil.e(TAG, "❌ 文章加载异常", e)
            updateState { it.copy(isLoading = false, error = e.message) }
        }
    }

    private suspend fun refresh() {
        updateState { it.copy(isLoading = true, error = null) }

        try {
            coroutineScope {
                // 刷新时建议并行等待，确保两者都尝试更新
                val bannersDeferred = async { getBannersUseCase() }
                val articlesDeferred = async { getArticlesUseCase(forceRefresh = true) }
                val unreadDeferred = async { getUnreadMessageCountUseCase() }

                bannersDeferred.await()
                unreadDeferred.await().onSuccess { count ->
                    updateState { it.copy(unreadCount = count) }
                }
                val articlesResult = articlesDeferred.await()

                articlesResult
                    .onSuccess {
                        updateState {
                            it.copy(
                                isLoading = false,
                                currentPage = 0,
                                hasMore = true
                            )
                        }
                    }
                    .onError { exception ->
                        handleException(exception)
                        updateState { it.copy(isLoading = false, error = exception.message) }
                    }
            }
        } catch (e: Exception) {
            LogUtil.e(TAG, "❌ 刷新失败", e)
            updateState { it.copy(isLoading = false, error = e.message) }
        }
    }

    private suspend fun loadMore() {
        val currentState = state.value
        if (currentState.isLoadingMore || !currentState.hasMore || currentState.isLoading) {
            LogUtil.d(TAG, "⏭️ 跳过: isLoadingMore=${currentState.isLoadingMore}, hasMore=${currentState.hasMore}")
            return
        }

        // 计算下一页：取当前最大页码 + 1
        val maxPage = currentState.articles.maxOfOrNull { it.page } ?: -1
        val nextPage = maxPage + 1

        LogUtil.d(TAG, "⬇️ 加载更多: nextPage=$nextPage, maxPage=$maxPage")
        updateState { it.copy(isLoadingMore = true) }

        loadMoreArticlesUseCase(nextPage)
            .onSuccess { newArticles ->
                LogUtil.d(TAG, "✅ 加载成功: page=$nextPage, size=${newArticles.size}")
                updateState {
                    it.copy(
                        isLoadingMore = false,
                        hasMore = newArticles.isNotEmpty()
                    )
                }
            }
            .onError { exception ->
                LogUtil.e(TAG, "❌ 加载失败: page=$nextPage, error=${exception.message}")
                handleException(exception)
                updateState { it.copy(isLoadingMore = false) }
            }
    }

    /**
     * 统一异常处理
     */
    private fun handleException(exception: AppException) {
        when (exception) {
            is AppException.Api.Unauthorized -> {
                emitEffect(HomeEffect.ShowMessage("请先登录"))
                emitEffect(HomeEffect.NavigateToLogin)
            }
            is AppException.Network.NoConnection,
            is AppException.Network.Timeout -> {
                emitEffect(HomeEffect.ShowMessage("网络连接失败，请检查网络"))
            }
            is AppException.Network.ServerError -> {
                emitEffect(HomeEffect.ShowMessage("服务器繁忙，请稍后重试"))
            }
            else -> {
                emitEffect(HomeEffect.ShowMessage(exception.message))
            }
        }
        errorHandler.handle(exception)
    }
}
