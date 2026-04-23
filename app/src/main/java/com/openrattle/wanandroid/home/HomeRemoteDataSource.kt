package com.openrattle.wanandroid.home

import com.openrattle.base.model.Article
import com.openrattle.base.model.asDisplay
import com.openrattle.base.model.Banner
import com.openrattle.base.model.PagingResponse
import com.openrattle.core.network.handleNetworkList
import com.openrattle.core.network.handleNetworkNonNull
import com.openrattle.core.network.WanService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 文章相关远程数据源
 */
@Singleton
class HomeRemoteDataSource @Inject constructor(
    private val service: WanService
) {
    suspend fun getBanners(): Result<List<Banner>> =
        handleNetworkList { service.getBanners() }

    suspend fun getTopArticles(): Result<List<Article>> = 
        handleNetworkList { service.getTopArticles() }.map { list ->
            list.map { it.copy(isTop = true).asDisplay() }
        }

    suspend fun getArticles(page: Int): Result<PagingResponse<Article>> = 
        handleNetworkNonNull { service.getArticles(page) }.map { paging ->
            paging.copy(datas = paging.datas.map { it.asDisplay() })
        }

    suspend fun getHomeData(): Result<List<Article>> {
        val topResult = getTopArticles()
        val listResult = getArticles(0)
        
        if (topResult.isFailure) return topResult
        if (listResult.isFailure) return Result.failure(listResult.exceptionOrNull()!!)
        
        val topArticles = topResult.getOrNull() ?: emptyList()
        val normalArticles = listResult.getOrNull()?.datas ?: emptyList()
        
        return Result.success(topArticles + normalArticles)
    }
}
