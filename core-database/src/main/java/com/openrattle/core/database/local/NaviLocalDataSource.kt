package com.openrattle.core.database.local

import com.openrattle.base.common.Constants
import com.openrattle.base.model.Article
import com.openrattle.base.model.asDisplay
import com.openrattle.base.model.Navi
import com.openrattle.core.database.dao.WanDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NaviLocalDataSource(
    private val wanDao: WanDao
) {
    val naviFlow: Flow<List<Navi>> = combine(
        wanDao.getNaviCategories(),
        wanDao.getAllNaviArticles()
    ) { categories, articles ->
        categories.map { category ->
            val categoryArticles = articles
                .filter { it.cid == category.cid }
                .map {
                    Article(
                        id = it.articleId,
                        title = it.title,
                        link = it.link
                    ).asDisplay()
                }
            category.toDomain(categoryArticles)
        }
    }

    suspend fun hasCache(): Boolean = withContext(Dispatchers.IO) {
        val categories = wanDao.getNaviCategories().first()
        if (categories.isEmpty()) return@withContext false
        
        val lastCacheTime = wanDao.getNaviCacheTime() ?: 0L
        val isExpired = System.currentTimeMillis() - lastCacheTime > Constants.CACHE_EXPIRATION_TIME
        !isExpired
    }

    suspend fun refreshNavi(navis: List<Navi>) {
        val categories = navis.map { it.toCategoryEntity() }
        val articles = navis.flatMap { it.toArticleEntities() }
        wanDao.refreshNavi(categories, articles)
    }
}