package com.openrattle.wanandroid.qa

import com.openrattle.base.model.Article
import com.openrattle.base.model.PagingResponse
import com.openrattle.core.database.local.QaLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QaRepository @Inject constructor(
    private val remote: QaRemoteDataSource,
    private val local: QaLocalDataSource
) {
    val cachedArticles: Flow<List<Article>> = local.cachedArticles

    suspend fun hasCache(): Boolean = local.hasCache()

    suspend fun hasPageCache(page: Int): Boolean = local.hasPageCache(page)

    suspend fun getCachedPage(page: Int): List<Article> = local.getArticlesByPage(page)

    suspend fun getQaList(page: Int): Result<PagingResponse<Article>> = remote.getQaList(page)

    suspend fun refreshCache(articles: List<Article>) {
        local.refreshArticles(articles)
    }

    suspend fun appendCache(page: Int, articles: List<Article>) {
        local.appendArticles(page, articles)
    }

    suspend fun clearCache() {
        local.clearArticles()
    }

    suspend fun updateCollect(id: Int, collect: Boolean) {
        local.updateCollect(id, collect)
    }
}
