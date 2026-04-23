package com.openrattle.wanandroid.navi

import com.openrattle.base.model.Navi
import com.openrattle.base.model.asDisplay
import com.openrattle.core.network.WanService
import com.openrattle.core.network.handleNetworkList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NaviRemoteDataSource @Inject constructor(
    private val service: WanService
) {
    suspend fun getNaviList(): Result<List<Navi>> =
        handleNetworkList { service.getNaviList() }.map { list ->
            list.map { navi ->
                navi.copy(articles = navi.articles.map { it.asDisplay() })
            }
        }
}
