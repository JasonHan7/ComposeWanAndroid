package com.openrattle.wanandroid.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.openrattle.base.model.Banner
import kotlinx.coroutines.delay

@Composable
fun BannerCarousel(
    banners: List<Banner>,
    isLoading: Boolean,
    onBannerClick: (Banner) -> Unit,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) {
        if (isLoading) {
            // 展示占位 Skeleton 状态，避免布局抖动
            BannerSkeleton(modifier)
        }
        // 如果不处于加载中且数据为空，则不占位（或者显示一个提示，但首页通常直接隐藏）
        return
    }

    val context = LocalContext.current
    val realCount = banners.size
    // 使用较大的虚拟数量实现无限滚动效果
    val virtualCount = realCount * 1000
    // 初始位置设在中间，并对齐到第一个 banner
    val initialPage = (virtualCount / 2).let { it - it % realCount }
    
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { virtualCount }
    )

    // 自动轮播 - 仅在非拖拽状态且 banner 数量大于 1 时开启
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    
    LaunchedEffect(isDragged, banners) {
        if (!isDragged && realCount > 1) {
            while (true) {
                delay(5000)
                try {
                    val nextPage = pagerState.currentPage + 1
                    if (nextPage < virtualCount) {
                        pagerState.animateScrollToPage(nextPage)
                    } else {
                        // 接近极值时，静默跳转回中间位置保持无限循环
                        pagerState.scrollToPage(initialPage)
                    }
                } catch (e: Exception) {
                    break
                }
            }
        }
    }
    
    // 轮播图主体
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1
        ) { page ->
            // 通过取模获取真实数据索引
            val banner = banners[page % realCount]
            
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onBannerClick(banner) },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val imageRequest = remember(banner.imagePath) {
                        ImageRequest.Builder(context)
                            .data(banner.imagePath)
                            .crossfade(true)
                            .build()
                    }
                    AsyncImage(
                        model = imageRequest,
                        contentDescription = banner.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = androidx.compose.ui.graphics.painter.ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                        error = androidx.compose.ui.graphics.painter.ColorPainter(MaterialTheme.colorScheme.errorContainer)
                    )
                    
                    // 底部文字阴影遮罩
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = banner.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Banner 加载中占位组件
 * 
 * 职责：
 * 1. 预留 180dp 高度，防止 Banner 异步加载完成后导致列表内容抖动（Layout Shift）
 * 2. 提供视觉反馈，提升用户体验
 */
@Composable
private fun BannerSkeleton(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(vertical = 4.dp), // 稍微留出一点阴影空间
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 这里可以添加闪烁动画，此处简记为一个带色块的占位
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    )
            )
        }
    }
}
