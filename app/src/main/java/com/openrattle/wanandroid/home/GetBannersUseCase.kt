package com.openrattle.wanandroid.home

import com.openrattle.base.utils.LogUtil
import com.openrattle.base.model.Banner
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val TAG = "GetBannersUC"

/**
 * 获取首页 Banner UseCase
 * 
 * 职责：
 * 1. 业务逻辑编排：从远程获取并同步到本地缓存
 * 2. 暴露数据流（Flow）实现离线优先加载
 */
class GetBannersUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    /**
     * 所有 Banner Flow（Room 自动更新）
     */
    val allBanners: Flow<List<Banner>> = repository.allBanners

    /**
     * 执行获取 Banner 逻辑
     * 
     * @return 操作结果
     */
    suspend operator fun invoke(): Result<Unit> {
        LogUtil.d(TAG, "🚀 获取 Banner 数据")
        return try {
            val result = repository.getBanners()
            if (result.isSuccess) {
                val banners = result.getOrNull() ?: emptyList()
                repository.saveBanners(banners)
                Result.success(Unit)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("未知错误"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
