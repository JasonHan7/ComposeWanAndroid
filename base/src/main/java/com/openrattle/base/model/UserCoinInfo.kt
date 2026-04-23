package com.openrattle.base.model

import kotlinx.serialization.Serializable

@Serializable
data class UserCoinInfo(
    val coinCount: Int,
    val level: Int,
    val nickname: String,
    val rank: String,
    val userId: Int,
    val username: String
)