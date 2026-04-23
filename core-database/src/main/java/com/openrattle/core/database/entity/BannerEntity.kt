package com.openrattle.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val imagePath: String,
    val url: String,
    val order: Int
)