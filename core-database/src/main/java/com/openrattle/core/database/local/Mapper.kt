package com.openrattle.core.database.local

import com.openrattle.base.model.Article
import com.openrattle.base.model.asDisplay
import com.openrattle.base.model.Banner
import com.openrattle.base.model.Navi
import com.openrattle.core.database.entity.ArticleEntity
import com.openrattle.core.database.entity.BannerEntity
import com.openrattle.core.database.entity.NaviArticleEntity
import com.openrattle.core.database.entity.NaviCategoryEntity
import com.openrattle.core.database.entity.PlazaArticleEntity
import com.openrattle.core.database.entity.QaArticleEntity

// ==================== Article 转换 ====================

fun Article.toEntity(isTop: Boolean = false) = ArticleEntity(
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
    isTop = isTop,
    page = page,
    displayTitle = displayTitle,
    displayAuthor = displayAuthor,
    displayDesc = displayDesc
)

fun ArticleEntity.toDomain() = Article(
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
    isTop = isTop,
    page = page,
    displayTitle = displayTitle,
    displayAuthor = displayAuthor,
    displayDesc = displayDesc
)

fun Article.toPlazaEntity() = PlazaArticleEntity(
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
    page = page,
    displayTitle = displayTitle,
    displayAuthor = displayAuthor,
    displayDesc = displayDesc
)

fun PlazaArticleEntity.toDomain() = Article(
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
    page = page,
    displayTitle = displayTitle,
    displayAuthor = displayAuthor,
    displayDesc = displayDesc
)

fun Article.toQaEntity() = QaArticleEntity(
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
    page = page,
    displayTitle = displayTitle,
    displayAuthor = displayAuthor,
    displayDesc = displayDesc
)

fun QaArticleEntity.toDomain() = Article(
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
    page = page,
    displayTitle = displayTitle,
    displayAuthor = displayAuthor,
    displayDesc = displayDesc
)

// ==================== Banner 转换 ====================

fun Banner.toEntity() = BannerEntity(
    id = id,
    title = title,
    imagePath = imagePath,
    url = url,
    order = order
)

fun BannerEntity.toDomain() = Banner(
    id = id,
    title = title,
    imagePath = imagePath,
    url = url,
    order = order
)

// ==================== Navi 转换 ====================

fun Navi.toCategoryEntity() = NaviCategoryEntity(cid = cid, name = name)

fun Navi.toArticleEntities() = articles.map { 
    NaviArticleEntity(
        cid = cid,
        articleId = it.id,
        title = it.title,
        link = it.link
    )
}

fun NaviCategoryEntity.toDomain(articles: List<Article>) = Navi(
    cid = cid,
    name = name,
    articles = articles
)

fun NaviArticleEntity.toDomain() = Article(
    id = articleId,
    title = title,
    link = link
).asDisplay()
