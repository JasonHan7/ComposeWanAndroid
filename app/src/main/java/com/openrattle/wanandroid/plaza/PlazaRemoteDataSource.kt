package com.openrattle.wanandroid.plaza

import com.openrattle.base.model.Article
import com.openrattle.base.model.asDisplay
import com.openrattle.base.model.PagingResponse
import com.openrattle.core.network.handleNetwork
import com.openrattle.core.network.handleNetworkNonNull
import com.openrattle.core.network.WanService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlazaRemoteDataSource @Inject constructor(
    private val service: WanService
) {
    suspend fun getPlazaList(page: Int): Result<PagingResponse<Article>> = 
        handleNetworkNonNull { service.getPlazaList(page) }.map { paging ->
            paging.copy(datas = paging.datas.map { it.asDisplay() })
        }

    suspend fun shareArticle(title: String, link: String): Result<Unit> =
        handleNetwork { service.shareArticle(title, link) }.map { }
}
