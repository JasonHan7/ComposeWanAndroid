package com.openrattle.wanandroid.navi

import com.openrattle.base.model.Navi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNaviUseCase @Inject constructor(
    private val repository: NaviRepository
) {
    val naviFlow: Flow<List<Navi>> = repository.naviFlow

    suspend operator fun invoke(forceRefresh: Boolean = false): Result<Unit> {
        if (forceRefresh) {
            return repository.refreshNavi()
        }

        if (repository.hasCache()) {
            return Result.success(Unit)
        }

        return repository.refreshNavi()
    }

    suspend fun refresh(): Result<Unit> = repository.refreshNavi()
}
