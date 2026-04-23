package com.openrattle.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "navi_categories")
data class NaviCategoryEntity(
    @PrimaryKey val cid: Int,
    val name: String,
    val cacheTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "navi_articles", primaryKeys = ["cid", "articleId"])
data class NaviArticleEntity(
    val cid: Int,
    val articleId: Int,
    val title: String,
    val link: String
)
