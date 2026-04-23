package com.openrattle.wanandroid.message

import com.openrattle.base.model.Message

data class MessageState(
    val readMessages: List<Message> = emptyList(),
    val unreadMessages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val currentPage: Int = 1, // 消息接口分页从 1 开始
    val error: String? = null
)

sealed class MessageIntent {
    data object LoadData : MessageIntent()
    data object Refresh : MessageIntent()
    data object LoadMore : MessageIntent()
}

sealed class MessageEffect {
    data class ShowMessage(val message: String) : MessageEffect()
}
