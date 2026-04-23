package com.openrattle.wanandroid.home

import com.openrattle.base.utils.LogUtil
import com.openrattle.base.model.Article
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val TAG = "GetArticlesUC"

/**
 * 获取首页文章 UseCase（简化版）
 * 
 * 两级缓存策略：
 * 1. 优先检查 Room，有数据先返回，后台刷新
 * 2. 无数据则网络请求，保存后返回
 */
class GetArticlesUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    /**
     * 所有文章 Flow（Room 自动更新）
     */
    val allArticles: Flow<List<Article>> = repository.allArticles

    /**
     * 执行获取首页数据
     * 
     * @param forceRefresh 是否强制从网络刷新
     * @return 操作结果（如果是异步刷新，返回成功表示刷新已发起）
     */
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<Unit> {
        LogUtil.d(TAG, "🚀 获取首页: forceRefresh=$forceRefresh, hasCache=${repository.hasCache()}")

        // 1. 强制刷新：直接同步网络请求
        if (forceRefresh) {
            LogUtil.d(TAG, "🔄 强制刷新")
            return refreshFromNetwork()
        }

        // 2. 有缓存：直接返回成功，由外部决定是否发起后台刷新
        if (repository.hasCache()) {
            LogUtil.d(TAG, "💾 命中缓存")
            return Result.success(Unit)
        }

        // 3. 无缓存：同步网络请求
        LogUtil.d(TAG, "🌐 无缓存，请求网络")
        return refreshFromNetwork()
    }

    /**
     * 暴露给外部调用的异步刷新方法
     */
    suspend fun refresh(): Result<Unit> = refreshFromNetwork()

    /**
     * 从网络刷新首页数据（置顶 + 第0页）
     */
    private suspend fun refreshFromNetwork(): Result<Unit> {
        return try {
            val result = repository.getHomeData()
            if (result.isSuccess) {
                val articles = result.getOrNull() ?: emptyList()
                // 使用 syncHomeData 统一替换置顶和第0页数据
                repository.syncHomeData(articles)
                Result.success(Unit)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("未知错误"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
