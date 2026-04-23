package com.openrattle.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_articles")
data class HistoryArticleEntity(
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
    val displayTitle: String = "",
    val displayAuthor: String = "",
    val displayDesc: String? = null,
    val viewTime: Long = System.currentTimeMillis() // 浏览时间戳，用于排序
)
