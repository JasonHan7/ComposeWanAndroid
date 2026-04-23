package com.openrattle.wanandroid.qa

import com.openrattle.base.model.Article
import com.openrattle.base.model.asDisplay
import com.openrattle.base.model.PagingResponse
import com.openrattle.core.network.WanService
import com.openrattle.core.network.handleNetworkNonNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QaRemoteDataSource @Inject constructor(
    private val service: WanService
) {
    suspend fun getQaList(page: Int): Result<PagingResponse<Article>> = 
        handleNetworkNonNull { service.getQaList(page) }.map { paging ->
            paging.copy(datas = paging.datas.map { it.asDisplay() })
        }
}
