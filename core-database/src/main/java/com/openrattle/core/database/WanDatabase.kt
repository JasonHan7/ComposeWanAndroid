package com.openrattle.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.openrattle.core.database.dao.WanDao
import com.openrattle.core.database.entity.ArticleEntity
import com.openrattle.core.database.entity.BannerEntity
import com.openrattle.core.database.entity.NaviArticleEntity
import com.openrattle.core.database.entity.NaviCategoryEntity
import com.openrattle.core.database.entity.PlazaArticleEntity
import com.openrattle.core.database.entity.QaArticleEntity
import com.openrattle.core.database.entity.HistoryArticleEntity

@Database(
    entities = [
        ArticleEntity::class,
        BannerEntity::class,
        NaviCategoryEntity::class,
        NaviArticleEntity::class,
        PlazaArticleEntity::class,
        QaArticleEntity::class,
        HistoryArticleEntity::class
    ],
    version = 9,
    exportSchema = false
)
abstract class WanDatabase : RoomDatabase() {
    abstract fun wanDao(): WanDao
}
