package com.openrattle.wanandroid.plaza

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openrattle.wanandroid.R
import com.openrattle.wanandroid.ui.components.NoRippleIconButton
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareArticleScreen(
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ShareArticleViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ShareArticleEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is ShareArticleEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }
                is ShareArticleEffect.NavigateBack -> {
                    // 对于页面来说，NavigateBack 代表操作成功，可以返回
                    onBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.share_article)) },
                navigationIcon = {
                    NoRippleIconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.dispatch(ShareArticleIntent.ShareArticle(title, link)) },
                        enabled = !state.isSharing && title.isNotBlank() && link.isNotBlank()
                    ) {
                        Icon(Icons.Default.Done, contentDescription = stringResource(R.string.submit))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.article_title)) },
                placeholder = { Text(stringResource(R.string.enter_article_title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !state.isSharing
            )
            
            OutlinedTextField(
                value = link,
                onValueChange = { link = it },
                label = { Text(stringResource(R.string.article_link)) },
                placeholder = { Text("https://...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !state.isSharing
            )

            if (state.isSharing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            
            Text(
                text = stringResource(R.string.share_article_tips),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.2f
            )
        }
    }
}
