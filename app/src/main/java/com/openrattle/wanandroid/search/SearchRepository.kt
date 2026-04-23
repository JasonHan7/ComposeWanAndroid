package com.openrattle.wanandroid.search

import com.openrattle.base.model.Article
import com.openrattle.base.model.HotKey
import com.openrattle.base.model.PagingResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val remote: SearchRemoteDataSource
) {
    suspend fun search(page: Int, keyword: String): Result<PagingResponse<Article>> = 
        remote.search(page, keyword)

    suspend fun getHotKeys(): Result<List<HotKey>> = remote.getHotKeys()
}
