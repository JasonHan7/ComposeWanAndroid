package com.openrattle.base.model

import kotlinx.serialization.Serializable

@Serializable
data class HotKey(
    val id: Int,
    val link: String,
    val name: String,
    val order: Int,
    val visible: Int
)