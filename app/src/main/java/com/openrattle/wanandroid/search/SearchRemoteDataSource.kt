package com.openrattle.wanandroid.search

import com.openrattle.base.model.Article
import com.openrattle.base.model.asDisplay
import com.openrattle.base.model.HotKey
import com.openrattle.base.model.PagingResponse
import com.openrattle.core.network.WanService
import com.openrattle.core.network.handleNetworkList
import com.openrattle.core.network.handleNetworkNonNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRemoteDataSource @Inject constructor(
    private val service: WanService
) {
    suspend fun search(page: Int, keyword: String): Result<PagingResponse<Article>> = 
        handleNetworkNonNull { service.search(page, keyword) }.map { paging ->
            paging.copy(datas = paging.datas.map { it.asDisplay() })
        }

    suspend fun getHotKeys(): Result<List<HotKey>> = 
        handleNetworkList { service.getHotKeys() }
}
