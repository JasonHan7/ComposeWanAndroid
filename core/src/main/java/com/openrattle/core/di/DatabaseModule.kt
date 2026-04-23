package com.openrattle.core.di

import android.content.Context
import androidx.room.Room
import com.openrattle.core.database.dao.WanDao
import com.openrattle.core.database.WanDatabase
import com.openrattle.core.database.local.ArticleLocalDataSource
import com.openrattle.core.database.local.BannerLocalDataSource
import com.openrattle.core.database.local.NaviLocalDataSource
import com.openrattle.core.database.local.PlazaLocalDataSource
import com.openrattle.core.database.local.QaLocalDataSource
import com.openrattle.core.database.local.HistoryLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WanDatabase {
        return Room.databaseBuilder(
            context,
            WanDatabase::class.java,
            "wan_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideWanDao(database: WanDatabase): WanDao {
        return database.wanDao()
    }

    @Provides
    @Singleton
    fun provideArticleLocalDataSource(dao: WanDao): ArticleLocalDataSource {
        return ArticleLocalDataSource(dao)
    }

    @Provides
    @Singleton
    fun provideBannerLocalDataSource(dao: WanDao): BannerLocalDataSource {
        return BannerLocalDataSource(dao)
    }

    @Provides
    @Singleton
    fun provideNaviLocalDataSource(dao: WanDao): NaviLocalDataSource {
        return NaviLocalDataSource(dao)
    }

    @Provides
    @Singleton
    fun providePlazaLocalDataSource(dao: WanDao): PlazaLocalDataSource {
        return PlazaLocalDataSource(dao)
    }

    @Provides
    @Singleton
    fun provideQaLocalDataSource(dao: WanDao): QaLocalDataSource {
        return QaLocalDataSource(dao)
    }

    @Provides
    @Singleton
    fun provideHistoryLocalDataSource(dao: WanDao): HistoryLocalDataSource {
        return HistoryLocalDataSource(dao)
    }
}
