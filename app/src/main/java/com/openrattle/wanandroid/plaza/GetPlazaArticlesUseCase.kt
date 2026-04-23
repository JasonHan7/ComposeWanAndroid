package com.openrattle.wanandroid.plaza

import com.openrattle.base.model.Article
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取广场文章 UseCase
 * 
 * 离线优先策略：
 * 1. 优先检查缓存，有则返回成功，触发异步刷新
 * 2. 无缓存或强制刷新，则同步网络请求并保存
 */
class GetPlazaArticlesUseCase @Inject constructor(
    private val repository: PlazaRepository
) {
    /**
     * 订阅广场文章流
     */
    val cachedArticles: Flow<List<Article>> = repository.cachedArticles

    /**
     * 执行获取广场数据
     * 
     * @param forceRefresh 是否强制从网络刷新
     * @return Result<Boolean>，Boolean 表示是否已经加载完所有数据（over）
     */
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<Boolean> {
        // 1. 强制刷新
        if (forceRefresh) {
            return refreshFromNetwork()
        }

        // 2. 有缓存：立即返回，告知外部暂未加载完（以触发后续加载或静默刷新）
        if (repository.hasCache()) {
            return Result.success(false)
        }

        // 3. 无缓存：同步请求
        return refreshFromNetwork()
    }

    /**
     * 暴露给外部调用的异步刷新方法
     */
    suspend fun refresh(): Result<Boolean> = refreshFromNetwork()

    private suspend fun refreshFromNetwork(): Result<Boolean> {
        return repository.getPlazaList(0)
            .map { response ->
                repository.refreshCache(response.datas)
                response.over
            }
    }
}
