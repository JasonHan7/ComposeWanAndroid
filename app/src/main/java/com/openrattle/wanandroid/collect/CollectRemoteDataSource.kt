package com.openrattle.wanandroid.collect

import com.openrattle.base.model.Article
import com.openrattle.base.model.asDisplay
import com.openrattle.base.model.PagingResponse
import com.openrattle.core.network.handleNetwork
import com.openrattle.core.network.handleNetworkNonNull
import com.openrattle.core.network.WanService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectRemoteDataSource @Inject constructor(
    private val service: WanService
) {
    suspend fun getCollectList(page: Int): Result<PagingResponse<Article>> = 
        handleNetworkNonNull { service.getCollectList(page) }.map { paging ->
            paging.copy(datas = paging.datas.map { it.asDisplay() })
        }

    suspend fun collect(id: Int): Result<Unit> = 
        handleNetwork { service.collect(id) }.map { }

    suspend fun uncollectFromList(id: Int, originId: Int = -1): Result<Unit> = 
        handleNetwork { service.uncollectList(id, originId) }.map { }

    suspend fun uncollectFromArticle(id: Int): Result<Unit> = 
        handleNetwork { service.uncollectOriginId(id) }.map { }
}
