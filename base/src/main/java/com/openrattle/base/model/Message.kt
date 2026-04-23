package com.openrattle.base.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 消息模型
 * 
 * 对应玩安卓消息接口返回的单条数据
 */
@Serializable
data class Message(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("message") val message: String,
    @SerialName("niceDate") val niceDate: String,
    @SerialName("date") val date: Long, // 消息发布的时间戳
    @SerialName("fromUser") val fromUser: String,
    @SerialName("fromUserId") val fromUserId: Int,
    @SerialName("userId") val userId: Int,
    @SerialName("tag") val tag: String,
    @SerialName("fullLink") val fullLink: String = "",
    @SerialName("link") val link: String = "",
    @SerialName("isRead") val isRead: Int,
    @SerialName("category") val category: Int
)
