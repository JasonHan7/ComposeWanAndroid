package com.openrattle.wanandroid.qa

import com.openrattle.base.model.Article
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取问答列表 UseCase
 */
class GetQaListUseCase @Inject constructor(
    private val repository: QaRepository
) {
    /**
     * 订阅问答文章流
     */
    val cachedArticles: Flow<List<Article>> = repository.cachedArticles

    /**
     * 执行获取问答数据
     * 
     * @param forceRefresh 是否强制从网络刷新
     * @return Result<Boolean>，Boolean 表示是否已经加载完所有数据（over）
     */
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<Boolean> {
        if (forceRefresh) {
            return refreshFromNetwork()
        }

        if (repository.hasCache()) {
            return Result.success(false)
        }

        return refreshFromNetwork()
    }

    suspend fun refresh(): Result<Boolean> = refreshFromNetwork()

    private suspend fun refreshFromNetwork(): Result<Boolean> {
        return repository.getQaList(0)
            .map { response ->
                repository.refreshCache(response.datas)
                response.over
            }
    }
}
