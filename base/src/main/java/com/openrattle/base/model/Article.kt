package com.openrattle.base.model

import androidx.core.text.HtmlCompat
import kotlinx.serialization.Serializable

/**
 * 文章领域模型
 * 
 * 注意：displayTitle, displayAuthor, displayDesc 等字段应在后台线程预处理填充，
 * 避免在 UI 线程进行耗时的 HTML 解析。
 */
@Serializable
data class Article(
    val id: Int,
    val title: String,
    val author: String? = null,
    val shareUser: String? = null,
    val link: String,
    val envelopePic: String? = null,
    val publishTime: Long? = null,
    val chapterName: String? = null,
    val superChapterName: String? = null,
    val superChapterId: Int? = null,
    val niceDate: String? = null,
    val collect: Boolean = false,
    val fresh: Boolean = false,
    val desc: String? = null,
    val originId: Int? = null,
    val isTop: Boolean = false,
    val page: Int = 0,
    // 预处理后的显示字段，给默认值以兼容 JSON 反序列化
    val displayTitle: String = "",
    val displayAuthor: String = "",
    val displayDesc: String? = null
)

/**
 * 预处理 HTML 标签和显示逻辑
 */
fun Article.asDisplay(): Article {
    val processedTitle = try {
        HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    } catch (e: Exception) {
        title
    }
    
    val processedAuthor = when {
        !author.isNullOrBlank() -> "作者：$author"
        !shareUser.isNullOrBlank() -> "分享人：$shareUser"
        else -> "匿名"
    }
        
    val processedDesc = desc?.let {
        try {
            HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        } catch (e: Exception) {
            it
        }
    }
    
    return copy(
        displayTitle = processedTitle,
        displayAuthor = processedAuthor,
        displayDesc = processedDesc
    )
}
