package com.openrattle.wanandroid.message

import com.openrattle.base.model.Message
import com.openrattle.base.model.PagingResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val remote: MessageRemoteDataSource
) {
    suspend fun getUnreadCount(): Result<Int> = remote.getUnreadCount()

    suspend fun getReadMessages(page: Int): Result<PagingResponse<Message>> =
        remote.getReadMessages(page)

    suspend fun getUnreadMessages(page: Int): Result<PagingResponse<Message>> =
        remote.getUnreadMessages(page)
}
