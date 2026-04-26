package com.openrattle.wanandroid.message

import com.openrattle.base.onError
import com.openrattle.core.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val getMessageListUseCase: GetMessageListUseCase
) : MviViewModel<MessageState, MessageIntent, MessageEffect>() {

    override fun initialState(): MessageState = MessageState()

    init {
        dispatch(MessageIntent.LoadData)
    }

    override suspend fun handleIntent(intent: MessageIntent) {
        when (intent) {
            is MessageIntent.LoadData -> loadData()
            is MessageIntent.Refresh -> refresh()
            is MessageIntent.LoadMore -> loadMore()
        }
    }

    private suspend fun loadData() {
        if (state.value.isLoading) return
        updateState { it.copy(isLoading = true, error = null) }

        // 先尝试获取未读消息（这会把所有消息标记为已读）
        getMessageListUseCase.getUnreadMessages(1)
            .onSuccess { unreadPaging ->
                val unreads = unreadPaging.datas
                // 获取已读消息第一页
                getMessageListUseCase.getReadMessages(1)
                    .onSuccess { readPaging ->
                        updateState { it.copy(
                            isLoading = false,
                            unreadMessages = unreads,
                            readMessages = readPaging.datas,
                            currentPage = 1,
                            hasMore = !readPaging.over
                        ) }
                    }
                    .onError { e ->
                        updateState { it.copy(
                            isLoading = false, 
                            unreadMessages = unreads,
                            error = e.message 
                        ) }
                    }
            }
            .onError {
                // 如果获取未读失败（可能是没登录或没有未读），尝试获取已读列表
                getMessageListUseCase.getReadMessages(1)
                    .onSuccess { readPaging ->
                        updateState { it.copy(
                            isLoading = false,
                            readMessages = readPaging.datas,
                            currentPage = 1,
                            hasMore = !readPaging.over
                        ) }
                    }
                    .onError { e ->
                        updateState { it.copy(isLoading = false, error = e.message) }
                    }
            }
    }

    private suspend fun refresh() {
        loadData()
    }

    private suspend fun loadMore() {
        val currentState = state.value
        if (currentState.isLoadingMore || !currentState.hasMore) return

        val nextPage = currentState.currentPage + 1
        updateState { it.copy(isLoadingMore = true) }

        getMessageListUseCase.getReadMessages(nextPage)
            .onSuccess { paging ->
                updateState { it.copy(
                    isLoadingMore = false,
                    readMessages = currentState.readMessages + paging.datas,
                    currentPage = nextPage,
                    hasMore = !paging.over
                ) }
            }
            .onError { e ->
                updateState { it.copy(isLoadingMore = false) }
                emitEffect(MessageEffect.ShowMessage(e.message))
            }
    }
}
