package com.openrattle.base.model

import kotlinx.serialization.Serializable

@Serializable
data class Banner(
    val id: Int,
    val title: String,
    val imagePath: String,
    val url: String,
    val order: Int
)