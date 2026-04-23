package com.openrattle.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.openrattle.core.database.entity.ArticleEntity
import com.openrattle.core.database.entity.BannerEntity
import com.openrattle.core.database.entity.HistoryArticleEntity
import com.openrattle.core.database.entity.NaviArticleEntity
import com.openrattle.core.database.entity.NaviCategoryEntity
import com.openrattle.core.database.entity.PlazaArticleEntity
import com.openrattle.core.database.entity.QaArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WanDao {
    @Query("SELECT * FROM articles ORDER BY isTop DESC, publishTime DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    // 按页查询文章（分页缓存）
    @Query("SELECT * FROM articles WHERE page = :page AND isTop = 0 ORDER BY publishTime DESC")
    fun getArticlesByPage(page: Int): Flow<List<ArticleEntity>>

    // 查询首页展示数据（置顶 + 所有普通页）
    @Query("SELECT * FROM articles ORDER BY isTop DESC, page, publishTime DESC")
    fun getHomeArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun getArticlesCount(): Int

    // 获取置顶文章数量
    @Query("SELECT COUNT(*) FROM articles WHERE isTop = 1")
    suspend fun getTopArticlesCount(): Int

    // 获取已缓存的最大页码
    @Query("SELECT MAX(page) FROM articles WHERE isTop = 0")
    suspend fun getMaxCachedPage(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles")
    suspend fun clearArticles()

    // 删除指定页的普通文章
    @Query("DELETE FROM articles WHERE page = :page AND isTop = 0")
    suspend fun clearArticlesByPage(page: Int)

    // 删除所有置顶文章
    @Query("DELETE FROM articles WHERE isTop = 1")
    suspend fun clearTopArticles()

    // 刷新指定页的普通文章（事务操作）
    @Transaction
    suspend fun replaceArticlesByPage(page: Int, articles: List<ArticleEntity>) {
        clearArticlesByPage(page)
        insertArticles(articles)
    }

    // 刷新首页组合数据（清理所有旧文章，确保分页序列一致性）
    @Transaction
    suspend fun replaceHomeData(articles: List<ArticleEntity>) {
        clearArticles()
        insertArticles(articles)
    }

    @Query("SELECT * FROM banners ORDER BY `order` DESC")
    fun getAllBanners(): Flow<List<BannerEntity>>

    @Query("SELECT COUNT(*) FROM banners")
    suspend fun getBannersCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanners(banners: List<BannerEntity>)

    @Query("DELETE FROM banners")
    suspend fun clearBanners()

    @Transaction
    suspend fun refreshBanners(banners: List<BannerEntity>) {
        clearBanners()
        insertBanners(banners)
    }

    // 导航相关
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNaviCategories(categories: List<NaviCategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNaviArticles(articles: List<NaviArticleEntity>)

    @Query("DELETE FROM navi_categories")
    suspend fun clearNaviCategories()

    @Query("DELETE FROM navi_articles")
    suspend fun clearNaviArticles()

    @Transaction
    suspend fun refreshNavi(categories: List<NaviCategoryEntity>, articles: List<NaviArticleEntity>) {
        clearNaviCategories()
        clearNaviArticles()
        insertNaviCategories(categories)
        insertNaviArticles(articles)
    }

    @Query("SELECT * FROM navi_categories")
    fun getNaviCategories(): Flow<List<NaviCategoryEntity>>

    @Query("SELECT MAX(cacheTime) FROM navi_categories")
    suspend fun getNaviCacheTime(): Long?

    @Query("SELECT * FROM navi_articles")
    fun getAllNaviArticles(): Flow<List<NaviArticleEntity>>

    @Query("SELECT * FROM navi_articles WHERE cid = :cid")
    fun getNaviArticlesByCid(cid: Int): Flow<List<NaviArticleEntity>>

    // 广场相关
    @Query("SELECT * FROM plaza_articles ORDER BY page, publishTime DESC")
    fun getAllPlazaArticles(): Flow<List<PlazaArticleEntity>>

    // 按页查询广场文章
    @Query("SELECT * FROM plaza_articles WHERE page = :page ORDER BY publishTime DESC")
    fun getPlazaArticlesByPage(page: Int): Flow<List<PlazaArticleEntity>>

    @Query("SELECT COUNT(*) FROM plaza_articles")
    suspend fun getPlazaArticlesCount(): Int

    // 获取已缓存的最大页码
    @Query("SELECT MAX(page) FROM plaza_articles")
    suspend fun getMaxCachedPlazaPage(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlazaArticles(articles: List<PlazaArticleEntity>)

    @Query("DELETE FROM plaza_articles")
    suspend fun clearPlazaArticles()

    // 删除指定页的广场文章
    @Query("DELETE FROM plaza_articles WHERE page = :page")
    suspend fun clearPlazaArticlesByPage(page: Int)

    @Transaction
    suspend fun replacePlazaArticlesByPage(page: Int, articles: List<PlazaArticleEntity>) {
        clearPlazaArticlesByPage(page)
        insertPlazaArticles(articles)
    }

    @Transaction
    suspend fun refreshPlazaArticles(articles: List<PlazaArticleEntity>) {
        clearPlazaArticles()
        insertPlazaArticles(articles)
    }

    // 问答相关
    @Query("SELECT * FROM qa_articles ORDER BY page, publishTime DESC")
    fun getAllQaArticles(): Flow<List<QaArticleEntity>>

    @Query("SELECT * FROM qa_articles WHERE page = :page ORDER BY publishTime DESC")
    fun getQaArticlesByPage(page: Int): Flow<List<QaArticleEntity>>

    @Query("SELECT COUNT(*) FROM qa_articles")
    suspend fun getQaArticlesCount(): Int

    @Query("SELECT MAX(page) FROM qa_articles")
    suspend fun getMaxCachedQaPage(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQaArticles(articles: List<QaArticleEntity>)

    @Query("DELETE FROM qa_articles")
    suspend fun clearQaArticles()

    @Query("DELETE FROM qa_articles WHERE page = :page")
    suspend fun clearQaArticlesByPage(page: Int)

    @Transaction
    suspend fun replaceQaArticlesByPage(page: Int, articles: List<QaArticleEntity>) {
        clearQaArticlesByPage(page)
        insertQaArticles(articles)
    }

    @Transaction
    suspend fun refreshQaArticles(articles: List<QaArticleEntity>) {
        clearQaArticles()
        insertQaArticles(articles)
    }

    // 更新文章收藏状态
    @Query("UPDATE articles SET collect = :collect WHERE id = :id")
    suspend fun updateArticleCollect(id: Int, collect: Boolean)

    @Query("UPDATE plaza_articles SET collect = :collect WHERE id = :id")
    suspend fun updatePlazaArticleCollect(id: Int, collect: Boolean)

    @Query("UPDATE qa_articles SET collect = :collect WHERE id = :id")
    suspend fun updateQaArticleCollect(id: Int, collect: Boolean)

    // 浏览历史相关
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryArticle(article: HistoryArticleEntity)

    @Query("SELECT * FROM history_articles ORDER BY viewTime DESC")
    fun getAllHistoryArticles(): Flow<List<HistoryArticleEntity>>

    @Query("DELETE FROM history_articles WHERE id = :id")
    suspend fun deleteHistoryArticle(id: Int)

    @Query("DELETE FROM history_articles")
    suspend fun clearHistory()
}
