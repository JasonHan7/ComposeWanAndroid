package com.openrattle.wanandroid.plaza

import com.openrattle.base.AppException
import com.openrattle.base.onError
import com.openrattle.core.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShareArticleViewModel @Inject constructor(
    private val shareArticleUseCase: ShareArticleUseCase
) : MviViewModel<ShareArticleState, ShareArticleIntent, ShareArticleEffect>() {

    override fun initialState(): ShareArticleState = ShareArticleState()

    override suspend fun handleIntent(intent: ShareArticleIntent) {
        when (intent) {
            is ShareArticleIntent.ShareArticle -> shareArticle(intent.title, intent.link)
        }
    }

    private suspend fun shareArticle(title: String, link: String) {
        if (state.value.isSharing) return
        
        updateState { it.copy(isSharing = true) }
        
        shareArticleUseCase(title, link)
            .onSuccess {
                updateState { it.copy(isSharing = false) }
                emitEffect(ShareArticleEffect.ShowMessage("分享成功"))
                emitEffect(ShareArticleEffect.NavigateBack)
            }
            .onError { e ->
                updateState { it.copy(isSharing = false) }
                handleException(e)
            }
    }

    private fun handleException(exception: AppException) {
        when (exception) {
            is AppException.Api.Unauthorized -> {
                emitEffect(ShareArticleEffect.ShowMessage("请先登录"))
                emitEffect(ShareArticleEffect.NavigateToLogin)
            }
            else -> {
                emitEffect(ShareArticleEffect.ShowMessage(exception.message))
            }
        }
    }
}
