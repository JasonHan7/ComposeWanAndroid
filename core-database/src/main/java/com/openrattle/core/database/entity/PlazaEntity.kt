package com.openrattle.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plaza_articles")
data class PlazaArticleEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val author: String?,
    val shareUser: String?,
    val link: String,
    val envelopePic: String?,
    val publishTime: Long?,
    val chapterName: String?,
    val superChapterName: String?,
    val niceDate: String?,
    val collect: Boolean,
    val fresh: Boolean = false,
    val desc: String?,
    val page: Int = 0,  // 分页标记
    val displayTitle: String = "",
    val displayAuthor: String = "",
    val displayDesc: String? = null,
    val cacheTime: Long = System.currentTimeMillis()
)
