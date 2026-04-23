package com.openrattle.wanandroid.message

import com.openrattle.base.model.Message
import com.openrattle.base.model.PagingResponse
import javax.inject.Inject

class GetUnreadMessageCountUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(): Result<Int> = repository.getUnreadCount()
}

class GetMessageListUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    /**
     * 获取已读消息列表
     */
    suspend fun getReadMessages(page: Int): Result<PagingResponse<Message>> =
        repository.getReadMessages(page)

    /**
     * 获取未读消息列表
     * 注意：访问此方法对应的接口后，所有消息都会被标记为已读
     */
    suspend fun getUnreadMessages(page: Int): Result<PagingResponse<Message>> =
        repository.getUnreadMessages(page)
}
