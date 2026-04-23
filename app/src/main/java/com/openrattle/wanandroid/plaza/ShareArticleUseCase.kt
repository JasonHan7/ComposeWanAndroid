package com.openrattle.wanandroid.plaza

import javax.inject.Inject

/**
 * 分享文章 UseCase
 */
class ShareArticleUseCase @Inject constructor(
    private val repository: PlazaRepository
) {
    suspend operator fun invoke(title: String, link: String): Result<Unit> {
        if (title.isBlank()) return Result.failure(Exception("请输入标题"))
        if (link.isBlank()) return Result.failure(Exception("请输入链接"))
        return repository.shareArticle(title, link)
    }
}
