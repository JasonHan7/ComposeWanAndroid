package com.openrattle.wanandroid.collect

import com.openrattle.base.model.Article
import com.openrattle.base.model.PagingResponse
import com.openrattle.wanandroid.home.HomeRepository
import com.openrattle.wanandroid.plaza.PlazaRepository
import com.openrattle.wanandroid.qa.QaRepository
import javax.inject.Inject

/**
 * 文章收藏业务逻辑
 */
class CollectArticleUseCase @Inject constructor(
    private val collectRepository: CollectRepository,
    private val homeRepository: HomeRepository,
    private val plazaRepository: PlazaRepository,
    private val qaRepository: QaRepository
) {
    /**
     * 获取收藏列表
     */
    suspend fun getCollectList(page: Int): Result<PagingResponse<Article>> {
        return collectRepository.getCollectList(page)
    }

    /**
     * 收藏文章
     * 
     * @param id 文章 ID
     */
    suspend fun collect(id: Int): Result<Unit> {
        return collectRepository.collect(id).onSuccess {
            // 同步更新本地所有相关表
            syncLocalCollectStatus(id, true)
        }
    }

    /**
     * 从收藏列表取消收藏
     */
    suspend fun uncollectFromList(id: Int, originId: Int = -1): Result<Unit> {
        return collectRepository.uncollectFromList(id, originId).onSuccess {
            // originId 是在收藏列表中的原始文章 ID
            val targetId = if (originId != -1) originId else id
            syncLocalCollectStatus(targetId, false)
        }
    }

    /**
     * 从文章详情/列表取消收藏
     */
    suspend fun uncollectFromArticle(id: Int): Result<Unit> {
        return collectRepository.uncollectFromArticle(id).onSuccess {
            syncLocalCollectStatus(id, false)
        }
    }

    /**
     * 同步更新本地数据库中的收藏状态
     */
    private suspend fun syncLocalCollectStatus(id: Int, collect: Boolean) {
        homeRepository.updateCollect(id, collect)
        plazaRepository.updateCollect(id, collect)
        qaRepository.updateCollect(id, collect)
    }
}
