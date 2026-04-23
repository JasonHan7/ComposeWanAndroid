package com.openrattle.base.model

import com.openrattle.base.model.Article
import kotlinx.serialization.Serializable

@Serializable
data class Navi(
    val cid: Int,
    val name: String,
    val articles: List<Article>
)