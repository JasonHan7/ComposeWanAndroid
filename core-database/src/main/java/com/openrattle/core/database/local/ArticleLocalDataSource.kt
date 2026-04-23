package com.openrattle.core.database.local

import com.openrattle.base.model.Article
import com.openrattle.core.database.dao.WanDao
import com.openrattle.core.database.entity.ArticleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * 文章本地数据源（简化版）
 * 
 * 只负责 Room 数据库操作，不做内存缓存
 */
class ArticleLocalDataSource(
    private val dao: WanDao
) {
    /**
     * 所有文章 Flow（按置顶、页码、时间排序）
     */
    val allArticles: Flow<List<Article>> = dao.getHomeArticles()
        .map { list -> list.map { it.toDomain() } }

    /**
     * 获取指定页的文章
     */
    suspend fun getArticlesByPage(page: Int): List<Article> = withContext(Dispatchers.IO) {
        dao.getArticlesByPage(page).first().map { it.toDomain() }
    }

    /**
     * 检查是否有任何缓存
     */
    suspend fun hasAnyCache(): Boolean = withContext(Dispatchers.IO) {
        dao.getArticlesCount() > 0
    }

    /**
     * 检查指定页是否有缓存
     */
    suspend fun hasPageCache(page: Int): Boolean = withContext(Dispatchers.IO) {
        dao.getArticlesByPage(page).first().isNotEmpty()
    }

    /**
     * 获取已缓存的最大页码
     */
    suspend fun getMaxCachedPage(): Int = withContext(Dispatchers.IO) {
        dao.getMaxCachedPage() ?: -1
    }

    /**
     * 保存文章分页（替换模式）
     */
    suspend fun saveArticles(page: Int, articles: List<Article>, isTop: Boolean = false) =
        withContext(Dispatchers.IO) {
            val entities = articles.map { it.toEntity(page = page, isTop = isTop) }
            if (page == 0 && isTop) {
                // 如果是首页置顶数据，通常由 GetArticlesUseCase 统一调用 replaceHomeData 处理
                // 这里为了通用性提供单独插入，但建议使用专门的首页同步方法
                dao.insertArticles(entities)
            } else {
                dao.replaceArticlesByPage(page, entities)
            }
        }

    /**
     * 专门用于首页刷新的组合保存（清理旧置顶和第0页）
     */
    suspend fun replaceHomeData(articles: List<Article>) = withContext(Dispatchers.IO) {
        val entities = articles.map { 
            // 保持数据原有的 isTop 属性，但统一归类到 page 0
            it.toEntity(page = 0, isTop = it.isTop) 
        }
        dao.replaceHomeData(entities)
    }

    /**
     * 清空所有文章
     */
    suspend fun clearAll() = withContext(Dispatchers.IO) {
        dao.clearArticles()
    }

    /**
     * 更新收藏状态
     */
    suspend fun updateCollect(id: Int, collect: Boolean) = withContext(Dispatchers.IO) {
        dao.updateArticleCollect(id, collect)
    }

    private fun ArticleEntity.toDomain(): Article {
        return Article(
            id = id,
            title = title,
            author = author,
            shareUser = shareUser,
            link = link,
            envelopePic = envelopePic,
            publishTime = publishTime,
            chapterName = chapterName,
            superChapterName = superChapterName,
            niceDate = niceDate,
            collect = collect,
            fresh = fresh,
            desc = desc,
            isTop = isTop,
            page = page,
            displayTitle = displayTitle,
            displayAuthor = displayAuthor,
            displayDesc = displayDesc
        )
    }

    private fun Article.toEntity(page: Int, isTop: Boolean): ArticleEntity {
        return ArticleEntity(
            id = id,
            title = title,
            author = author,
            shareUser = shareUser,
            link = link,
            envelopePic = envelopePic,
            publishTime = publishTime,
            chapterName = chapterName,
            superChapterName = superChapterName,
            niceDate = niceDate,
            collect = collect,
            fresh = fresh,
            desc = desc,
            isTop = isTop,
            page = page,
            displayTitle = displayTitle,
            displayAuthor = displayAuthor,
            displayDesc = displayDesc
        )
    }
}
