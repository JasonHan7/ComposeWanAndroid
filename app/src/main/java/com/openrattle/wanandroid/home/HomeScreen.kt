package com.openrattle.wanandroid.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openrattle.wanandroid.R
import com.openrattle.common_ui.components.LoadingMoreItem
import com.openrattle.wanandroid.ui.components.ArticleItem
import kotlinx.coroutines.flow.collectLatest

/**
 * 首页屏幕
 *
 * 功能特性：
 * 1. 展示文章列表（支持下拉刷新、上拉加载更多）
 * 2. 顶部轮播图（Banner）展示
 * 3. 仿掘金风格搜索栏
 * 4. 错误状态处理
 *
 * @param onArticleClick 文章点击回调，传入文章链接 URL
 * @param onSearchClick 搜索按钮点击回调
 * @param viewModel 首页 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onArticleClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onMessageClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // ==================== 状态收集 ====================
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()

    // ==================== Side Effect 处理 ====================
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is HomeEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is HomeEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }
            }
        }
    }

    // ==================== 派生状态：加载更多触发条件 ====================
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            lastVisibleIndex > (totalItems - 2) && totalItems > 0
        }
    }

    LaunchedEffect(shouldLoadMore, state.isLoading, state.isLoadingMore, state.hasMore) {
        if (shouldLoadMore && !state.isLoading && !state.isLoadingMore && state.hasMore) {
            viewModel.dispatch(HomeIntent.LoadMore)
        }
    }

    // ==================== UI 结构 ====================
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    // 仿掘金风格搜索框
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                            .clickable { onSearchClick() }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.search_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    // 消息图标
                    IconButton(onClick = onMessageClick) {
                        BadgedBox(
                            badge = {
                                if (state.unreadCount > 0) {
                                    Badge {
                                        Text(text = if (state.unreadCount > 99) "99+" else state.unreadCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = "消息",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }
    ) { paddingValues ->
        /**
         * PullToRefreshBox 支持下拉刷新手势
         */
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.dispatch(HomeIntent.Refresh) },
            state = pullToRefreshState,
            modifier = Modifier.padding(paddingValues),
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = state.isLoading,
                    containerColor = MaterialTheme.colorScheme.surface,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            // 根据状态显示不同内容
            when {
                state.error != null && state.articles.isEmpty() && !state.isLoading -> {
                    state.error?.let {
                        ErrorContent(
                            error = it,
                            onRetry = { viewModel.dispatch(HomeIntent.LoadData) }
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        // Banner 轮播图
                        item {
                            BannerCarousel(
                                banners = state.banners,
                                isLoading = state.isLoading,
                                onBannerClick = { banner ->
                                    onArticleClick(banner.url)
                                }
                            )
                        }

                        /**
                         * 文章列表项
                         */
                        items(
                            items = state.articles,
                            key = { it.id }
                        ) { article ->
                            ArticleItem(
                                article = article,
                                onClick = {
                                    viewModel.dispatch(HomeIntent.SaveHistory(article))
                                    onArticleClick(article.link)
                                },
                                onCollectClick = {
                                    viewModel.dispatch(HomeIntent.ToggleCollect(article))
                                }
                            )
                        }

                        // 加载更多指示器
                        if (state.isLoadingMore) {
                            item {
                                LoadingMoreItem()
                            }
                        }

                        // 没有更多数据提示
                        if (!state.hasMore && state.articles.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.no_more_data),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }

                        // 底部间距
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * 错误内容展示组件
 */
@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "加载失败",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("重新加载")
        }
    }
}
