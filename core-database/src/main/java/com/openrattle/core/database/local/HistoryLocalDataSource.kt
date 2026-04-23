package com.openrattle.core.database.local

import com.openrattle.base.model.Article
import com.openrattle.core.database.dao.WanDao
import com.openrattle.core.database.entity.HistoryArticleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class HistoryLocalDataSource(
    private val dao: WanDao
) {
    val allHistory: Flow<List<Article>> = dao.getAllHistoryArticles().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun addHistory(article: Article) = withContext(Dispatchers.IO) {
        dao.insertHistoryArticle(article.toHistoryEntity())
    }

    suspend fun deleteHistory(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteHistoryArticle(id)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        dao.clearHistory()
    }

    private fun HistoryArticleEntity.toDomain(): Article {
        return Article(
            id = id,
            title = title,
            author = author,
            shareUser = shareUser,
            link = link,
            envelopePic = envelopePic,
            publishTime = publishTime,
            chapterName = chapterName,
            superChapterName = superChapterName,
            niceDate = niceDate,
            collect = collect,
            fresh = fresh,
            desc = desc,
            displayTitle = displayTitle,
            displayAuthor = displayAuthor,
            displayDesc = displayDesc
        )
    }

    private fun Article.toHistoryEntity(): HistoryArticleEntity {
        return HistoryArticleEntity(
            id = id,
            title = title,
            author = author,
            shareUser = shareUser,
            link = link,
            envelopePic = envelopePic,
            publishTime = publishTime,
            chapterName = chapterName,
            superChapterName = superChapterName,
            niceDate = niceDate,
            collect = collect,
            fresh = fresh,
            desc = desc,
            displayTitle = displayTitle,
            displayAuthor = displayAuthor,
            displayDesc = displayDesc,
            viewTime = System.currentTimeMillis()
        )
    }
}
