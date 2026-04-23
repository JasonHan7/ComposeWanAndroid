package com.openrattle.wanandroid.plaza

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openrattle.wanandroid.R
import com.openrattle.common_ui.components.LoadingMoreItem
import com.openrattle.wanandroid.ui.components.ArticleItem
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlazaScreen(
    onArticleClick: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToShare: () -> Unit,
    viewModel: PlazaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PlazaEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is PlazaEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }
                else -> {}
            }
        }
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            lastVisibleItemIndex > (totalItemsNumber - 2)
        }
    }

    LaunchedEffect(shouldLoadMore, state.isLoading, state.isLoadingMore, state.hasMore) {
        if (shouldLoadMore && !state.isLoading && !state.isLoadingMore && state.hasMore && state.articles.isNotEmpty()) {
            viewModel.dispatch(PlazaIntent.LoadMore)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.nav_plaza)) },
                actions = {
                    IconButton(onClick = onNavigateToShare) {
                        Icon(Icons.Default.Add, contentDescription = "分享文章")
                    }
                }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.dispatch(PlazaIntent.Refresh) },
            state = pullToRefreshState,
            modifier = Modifier.padding(padding),
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
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.articles, key = { it.id }) { article ->
                    ArticleItem(
                        article = article,
                        onClick = { 
                            viewModel.dispatch(PlazaIntent.SaveHistory(article))
                            onArticleClick(article.link) 
                        },
                        onCollectClick = {
                            viewModel.dispatch(PlazaIntent.ToggleCollect(article))
                        }
                    )
                }

                if (state.isLoadingMore) {
                    item {
                        LoadingMoreItem()
                    }
                }

                if (!state.hasMore && state.articles.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_more_data),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                state.error?.let { error ->
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.error_msg, error),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.dispatch(PlazaIntent.Refresh) }
                            ) {
                                Text("重新加载")
                            }
                        }
                    }
                }
            }
        }
    }
}
