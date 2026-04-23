package com.openrattle.wanandroid.home

import com.openrattle.core.database.local.ArticleLocalDataSource
import com.openrattle.core.database.local.BannerLocalDataSource
import com.openrattle.base.model.Article
import com.openrattle.base.model.Banner
import com.openrattle.base.model.PagingResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 首页Repository（简化版）
 * 
 * 职责：
 * 1. 协调远程和本地数据源
 * 2. 两级缓存：Room → 网络
 */
@Singleton
class HomeRepository @Inject constructor(
    private val remote: HomeRemoteDataSource,
    private val local: ArticleLocalDataSource,
    private val bannerLocal: BannerLocalDataSource
) {
    /**
     * 所有文章 Flow（Room 自动更新）
     */
    val allArticles: Flow<List<Article>> = local.allArticles

    /**
     * 所有 Banner Flow（Room 自动更新）
     */
    val allBanners: Flow<List<Banner>> = bannerLocal.allBanners

    /**
     * 获取 Banner
     */
    suspend fun getBanners(): Result<List<Banner>> = remote.getBanners()

    /**
     * 获取首页组合数据（置顶 + 第0页）
     */
    suspend fun getHomeData(): Result<List<Article>> = remote.getHomeData()

    /**
     * 获取指定页文章
     */
    suspend fun getArticles(page: Int): Result<PagingResponse<Article>> = 
        remote.getArticles(page)

    /**
     * 检查是否有任何缓存
     */
    suspend fun hasCache(): Boolean = local.hasAnyCache()

    /**
     * 检查是否有 Banner 缓存
     */
    suspend fun hasBannerCache(): Boolean = bannerLocal.hasCache()

    /**
     * 检查指定页是否有缓存
     */
    suspend fun hasPageCache(page: Int): Boolean = local.hasPageCache(page)

    /**
     * 获取指定页缓存（同步）
     */
    suspend fun getCachedPage(page: Int): List<Article> = 
        local.getArticlesByPage(page)

    /**
     * 获取已缓存的最大页码
     */
    suspend fun getMaxCachedPage(): Int = local.getMaxCachedPage()

    /**
     * 同步首页数据（置顶+第0页），清理旧数据
     */
    suspend fun syncHomeData(articles: List<Article>) {
        local.replaceHomeData(articles)
    }

    /**
     * 保存文章到本地（带页码）
     */
    suspend fun saveArticles(page: Int, articles: List<Article>, isTop: Boolean = false) {
        local.saveArticles(page, articles, isTop)
    }

    /**
     * 保存 Banner 到本地
     */
    suspend fun saveBanners(banners: List<Banner>) {
        bannerLocal.saveBanners(banners)
    }

    /**
     * 更新收藏状态
     */
    suspend fun updateCollect(id: Int, collect: Boolean) {
        local.updateCollect(id, collect)
    }

    /**
     * 清空所有缓存
     */
    suspend fun clearAllCache() {
        local.clearAll()
        bannerLocal.saveBanners(emptyList()) // 清空缓存
    }
}
