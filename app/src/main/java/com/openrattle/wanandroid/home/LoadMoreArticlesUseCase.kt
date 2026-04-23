package com.openrattle.wanandroid.home

import com.openrattle.base.utils.LogUtil
import com.openrattle.base.model.Article
import javax.inject.Inject

private const val TAG = "LoadMoreUC"

/**
 * 加载更多文章 UseCase（简化版）
 * 
 * 策略：
 * 1. 检查 Room 是否有该页缓存
 * 2. 有：直接返回，可选后台刷新
 * 3. 无：网络请求，保存到 Room
 */
class LoadMoreArticlesUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    /**
     * 执行加载更多
     * 
     * @param page 页码
     * @return 该页文章列表
     */
    suspend operator fun invoke(page: Int): Result<List<Article>> {
        LogUtil.d(TAG, "🔄 加载第 $page 页")

        // 1. 检查 Room 缓存
        if (repository.hasPageCache(page)) {
            LogUtil.d(TAG, "💾 命中 Room 缓存: page=$page")
            return Result.success(repository.getCachedPage(page))
        }

        // 2. 无缓存则网络请求
        LogUtil.d(TAG, "🌐 请求网络: page=$page")
        return repository.getArticles(page)
            .map { response ->
                LogUtil.d(TAG, "✅ 网络成功: page=$page, size=${response.datas.size}")
                response.datas.also { articles ->
                    // 保存到 Room
                    repository.saveArticles(page, articles, isTop = false)
                }
            }
    }
}
