package com.openrattle.wanandroid.message

import com.openrattle.base.model.Message
import com.openrattle.base.model.PagingResponse
import com.openrattle.core.network.WanService
import com.openrattle.core.network.handleNetworkNonNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRemoteDataSource @Inject constructor(
    private val service: WanService
) {
    suspend fun getUnreadCount(): Result<Int> =
        handleNetworkNonNull { service.getUnreadMessageCount() }

    suspend fun getReadMessages(page: Int): Result<PagingResponse<Message>> =
        handleNetworkNonNull { service.getReadMessageList(page) }

    suspend fun getUnreadMessages(page: Int): Result<PagingResponse<Message>> =
        handleNetworkNonNull { service.getUnreadMessageList(page) }
}
