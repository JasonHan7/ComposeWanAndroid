package com.openrattle.wanandroid.plaza

import com.openrattle.core.database.local.PlazaLocalDataSource
import com.openrattle.base.model.Article
import com.openrattle.base.model.PagingResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlazaRepository @Inject constructor(
    private val remote: PlazaRemoteDataSource,
    private val local: PlazaLocalDataSource
) {
    val cachedArticles: Flow<List<Article>> = local.cachedArticles

    suspend fun hasCache(): Boolean = local.hasCache()

    suspend fun hasPageCache(page: Int): Boolean = local.hasPageCache(page)

    suspend fun getCachedPage(page: Int): List<Article> = local.getArticlesByPage(page)

    suspend fun getPlazaList(page: Int): Result<PagingResponse<Article>> = remote.getPlazaList(page)

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

    suspend fun shareArticle(title: String, link: String): Result<Unit> =
        remote.shareArticle(title, link)
}
