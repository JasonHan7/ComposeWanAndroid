package com.openrattle.wanandroid.navi

import com.openrattle.base.model.Navi
import com.openrattle.core.database.local.NaviLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NaviRepository @Inject constructor(
    private val remote: NaviRemoteDataSource,
    private val local: NaviLocalDataSource
) {
    val naviFlow: Flow<List<Navi>> = local.naviFlow

    suspend fun hasCache(): Boolean = local.hasCache()

    suspend fun refreshNavi(): Result<Unit> {
        return remote.getNaviList().map { list ->
            local.refreshNavi(list)
        }
    }
}
