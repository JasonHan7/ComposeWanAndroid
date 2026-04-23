package com.openrattle.core.database.local

import com.openrattle.base.model.Banner
import com.openrattle.core.database.dao.WanDao
import com.openrattle.core.database.entity.BannerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BannerLocalDataSource(
    private val dao: WanDao
) {
    val allBanners: Flow<List<Banner>> = dao.getAllBanners()
        .map { list -> list.map { it.toDomain() } }

    suspend fun saveBanners(banners: List<Banner>) = withContext(Dispatchers.IO) {
        dao.refreshBanners(banners.map { it.toEntity() })
    }

    suspend fun hasCache(): Boolean = withContext(Dispatchers.IO) {
        dao.getBannersCount() > 0
    }

    private fun BannerEntity.toDomain(): Banner {
        return Banner(
            id = id,
            title = title,
            imagePath = imagePath,
            url = url,
            order = order
        )
    }

    private fun Banner.toEntity(): BannerEntity {
        return BannerEntity(
            id = id,
            title = title,
            imagePath = imagePath,
            url = url,
            order = order
        )
    }
}
