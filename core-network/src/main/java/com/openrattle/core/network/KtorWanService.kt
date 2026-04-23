package com.openrattle.core.network

import com.openrattle.base.model.Article
import com.openrattle.base.model.Banner
import com.openrattle.base.model.HotKey
import com.openrattle.base.model.Message
import com.openrattle.base.model.Navi
import com.openrattle.base.model.PagingResponse
import com.openrattle.base.model.UserCoinInfo
import com.openrattle.base.model.UserInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.parameters

/**
 * WanService 的 Ktor 实现
 */
class KtorWanService(
    private val client: HttpClient
) : WanService {

    private val baseUrl = "https://www.wanandroid.com"

    override suspend fun login(username: String, password: String): WanResponse<UserInfo> =
        client.submitForm(
            url = "$baseUrl/user/login",
            formParameters = parameters {
                append("username", username)
                append("password", password)
            }
        ).body()

    override suspend fun register(username: String, password: String, repassword: String): WanResponse<UserInfo> =
        client.submitForm(
            url = "$baseUrl/user/register",
            formParameters = parameters {
                append("username", username)
                append("password", password)
                append("repassword", repassword)
            }
        ).body()

    override suspend fun logout(): WanResponse<Unit> =
        client.get("$baseUrl/user/logout/json").body()

    override suspend fun getUserCoinInfo(): WanResponse<UserCoinInfo> =
        client.get("$baseUrl/lg/coin/userinfo/json").body()

    override suspend fun getHotKeys(): WanResponse<List<HotKey>> =
        client.get("$baseUrl/hotkey/json").body()

    override suspend fun getBanners(): WanResponse<List<Banner>> =
        client.get("$baseUrl/banner/json").body()

    override suspend fun getArticles(
        page: Int,
        pageSize: Int?
    ): WanResponse<PagingResponse<Article>> =
        client.get("$baseUrl/article/list/$page/json") {
            pageSize?.let { parameter("page_size", it) }
        }.body()

    override suspend fun getTopArticles(): WanResponse<List<Article>> =
        client.get("$baseUrl/article/top/json").body()

    override suspend fun search(
        page: Int,
        keyword: String,
        pageSize: Int?
    ): WanResponse<PagingResponse<Article>> =
        client.submitForm(
            url = "$baseUrl/article/query/$page/json",
            formParameters = parameters {
                append("k", keyword)
                pageSize?.let { append("page_size", it.toString()) }
            }
        ).body()

    override suspend fun collect(id: Int): WanResponse<Unit> =
        client.post("$baseUrl/lg/collect/$id/json").body()

    override suspend fun uncollectOriginId(id: Int): WanResponse<Unit> =
        client.post("$baseUrl/lg/uncollect_originId/$id/json").body()

    override suspend fun uncollectList(id: Int, originId: Int): WanResponse<Unit> =
        client.submitForm(
            url = "$baseUrl/lg/uncollect/$id/json",
            formParameters = parameters {
                append("originId", originId.toString())
            }
        ).body()

    override suspend fun getCollectList(page: Int): WanResponse<PagingResponse<Article>> =
        client.get("$baseUrl/lg/collect/list/$page/json").body()

    override suspend fun getQaList(page: Int): WanResponse<PagingResponse<Article>> =
        client.get("$baseUrl/wenda/list/$page/json").body()

    override suspend fun getNaviList(): WanResponse<List<Navi>> =
        client.get("$baseUrl/navi/json").body()

    override suspend fun getPlazaList(page: Int): WanResponse<PagingResponse<Article>> =
        client.get("$baseUrl/user_article/list/$page/json").body()

    override suspend fun shareArticle(title: String, link: String): WanResponse<Unit> =
        client.submitForm(
            url = "$baseUrl/lg/user_article/add/json",
            formParameters = parameters {
                append("title", title)
                append("link", link)
            }
        ).body()

    override suspend fun getUnreadMessageCount(): WanResponse<Int> =
        client.get("$baseUrl/message/lg/count_unread/json").body()

    override suspend fun getReadMessageList(page: Int): WanResponse<PagingResponse<Message>> =
        client.get("$baseUrl/message/lg/readed_list/$page/json").body()

    override suspend fun getUnreadMessageList(page: Int): WanResponse<PagingResponse<Message>> =
        client.get("$baseUrl/message/lg/unread_list/$page/json").body()
}
