package com.openrattle.core.network

import com.openrattle.base.model.Article
import com.openrattle.base.model.Banner
import com.openrattle.base.model.HotKey
import com.openrattle.base.model.Message
import com.openrattle.base.model.Navi
import com.openrattle.base.model.PagingResponse
import com.openrattle.base.model.UserCoinInfo
import com.openrattle.base.model.UserInfo

/**
 * 玩 Android API 服务接口
 * 与具体网络框架解耦，便于后续替换实现
 */
interface WanService {
    suspend fun login(username: String, password: String): WanResponse<UserInfo>
    suspend fun register(username: String, password: String, repassword: String): WanResponse<UserInfo>
    suspend fun logout(): WanResponse<Unit>
    suspend fun getUserCoinInfo(): WanResponse<UserCoinInfo>
    suspend fun getHotKeys(): WanResponse<List<HotKey>>
    suspend fun getBanners(): WanResponse<List<Banner>>
    suspend fun getArticles(page: Int, pageSize: Int? = null): WanResponse<PagingResponse<Article>>
    suspend fun getTopArticles(): WanResponse<List<Article>>
    suspend fun search(page: Int, keyword: String, pageSize: Int? = null): WanResponse<PagingResponse<Article>>
    suspend fun collect(id: Int): WanResponse<Unit>
    suspend fun uncollectOriginId(id: Int): WanResponse<Unit>
    suspend fun uncollectList(id: Int, originId: Int = -1): WanResponse<Unit>
    suspend fun getCollectList(page: Int): WanResponse<PagingResponse<Article>>
    suspend fun getQaList(page: Int): WanResponse<PagingResponse<Article>>
    suspend fun getNaviList(): WanResponse<List<Navi>>
    suspend fun getPlazaList(page: Int): WanResponse<PagingResponse<Article>>
    suspend fun shareArticle(title: String, link: String): WanResponse<Unit>
    
    // 消息相关
    suspend fun getUnreadMessageCount(): WanResponse<Int>
    suspend fun getReadMessageList(page: Int): WanResponse<PagingResponse<Message>>
    suspend fun getUnreadMessageList(page: Int): WanResponse<PagingResponse<Message>>
}
