package com.openrattle.core.network

import kotlinx.serialization.Serializable

@Serializable
data class WanResponse<T>(
    val data: T? = null,
    val errorCode: Int,
    val errorMsg: String
) {
    fun isSuccess() = errorCode == 0
}
