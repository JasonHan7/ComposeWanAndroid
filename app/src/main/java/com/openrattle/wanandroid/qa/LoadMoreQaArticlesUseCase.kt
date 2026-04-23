package com.openrattle.wanandroid.qa

import com.openrattle.base.model.Article
import javax.inject.Inject

/**
 * 加载更多问答文章 UseCase
 */
class LoadMoreQaArticlesUseCase @Inject constructor(
    private val repository: QaRepository
) {
    suspend operator fun invoke(page: Int): Result<Pair<List<Article>, Boolean>> {
        // 1. 检查本地分页缓存
        if (repository.hasPageCache(page)) {
            return Result.success(repository.getCachedPage(page) to false)
        }

        // 2. 请求网络并保存
        return repository.getQaList(page)
            .map { response ->
                response.datas.also { articles ->
                    repository.appendCache(page, articles)
                } to response.over
            }
    }
}
