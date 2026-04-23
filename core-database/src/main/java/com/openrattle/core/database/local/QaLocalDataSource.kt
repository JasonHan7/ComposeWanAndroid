package com.openrattle.core.database.local

import com.openrattle.base.model.Article
import com.openrattle.core.database.dao.WanDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class QaLocalDataSource(
    private val wanDao: WanDao
) {
    val cachedArticles: Flow<List<Article>> = wanDao.getAllQaArticles().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun hasCache(): Boolean = wanDao.getQaArticlesCount() > 0

    suspend fun hasPageCache(page: Int): Boolean = withContext(Dispatchers.IO) {
        wanDao.getQaArticlesByPage(page).first().isNotEmpty()
    }

    suspend fun getArticlesByPage(page: Int): List<Article> = withContext(Dispatchers.IO) {
        wanDao.getQaArticlesByPage(page).first().map { it.toDomain() }
    }

    suspend fun refreshArticles(articles: List<Article>) {
        wanDao.refreshQaArticles(articles.map { it.toQaEntity() })
    }

    suspend fun appendArticles(page: Int, articles: List<Article>) = withContext(Dispatchers.IO) {
        val entities = articles.map { 
            it.copy(page = page).toQaEntity() 
        }
        wanDao.replaceQaArticlesByPage(page, entities)
    }

    suspend fun clearArticles() {
        wanDao.clearQaArticles()
    }

    suspend fun updateCollect(id: Int, collect: Boolean) = withContext(Dispatchers.IO) {
        wanDao.updateQaArticleCollect(id, collect)
    }
}
