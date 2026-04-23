package com.openrattle.wanandroid.collect

import com.openrattle.base.model.Article
import com.openrattle.base.model.PagingResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectRepository @Inject constructor(
    private val remote: CollectRemoteDataSource
) {
    suspend fun getCollectList(page: Int): Result<PagingResponse<Article>> = 
        remote.getCollectList(page)

    suspend fun collect(id: Int): Result<Unit> = remote.collect(id)

    suspend fun uncollectFromList(id: Int, originId: Int = -1): Result<Unit> = 
        remote.uncollectFromList(id, originId)

    suspend fun uncollectFromArticle(id: Int): Result<Unit> = 
        remote.uncollectFromArticle(id)
}
