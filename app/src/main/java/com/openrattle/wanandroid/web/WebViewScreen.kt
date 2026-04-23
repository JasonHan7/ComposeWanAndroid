package com.openrattle.wanandroid.web

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.openrattle.wanandroid.BuildConfig
import com.openrattle.wanandroid.R
import com.openrattle.wanandroid.ui.components.NoRippleIconButton
import com.openrattle.core.utils.WebViewManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    title: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var webView: WebView? by remember { mutableStateOf(null) }
    var currentTitle by remember { mutableStateOf(title) }
    var canGoBack by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var isReaderMode by remember { mutableStateOf(false) }

    // 开启调试（仅在 Debug 模式下）
    if (BuildConfig.DEBUG) {
        WebView.setWebContentsDebuggingEnabled(true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentTitle,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1
                        )
                        Text(
                            text = webView?.url ?: url,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    Row {
                        NoRippleIconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                        if (canGoBack) {
                            NoRippleIconButton(onClick = { 
                                webView?.stopLoading()
                                onBack() 
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.close)
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.more)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.refresh)) },
                            onClick = {
                                webView?.reload()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isReaderMode) stringResource(R.string.exit_reader_mode) else stringResource(R.string.reader_mode)) },
                            onClick = {
                                if (isReaderMode) {
                                    webView?.reload()
                                    isReaderMode = false
                                } else {
                                    val js = """
                                        (function() {
                                            const articleTags = ['article', 'main', '.article', '.post', '.content', '.entry-content', '.article-content'];
                                            let mainContent = null;
                                            for (let tag of articleTags) {
                                                mainContent = document.querySelector(tag);
                                                if (mainContent) break;
                                            }
                                            if (!mainContent) {
                                                const divs = document.querySelectorAll('div');
                                                let maxPs = 0;
                                                divs.forEach(div => {
                                                    const pCount = div.querySelectorAll('p').length;
                                                    if (pCount > maxPs) {
                                                        maxPs = pCount;
                                                        mainContent = div;
                                                    }
                                                });
                                            }
                                            if (mainContent) {
                                                const content = mainContent.innerHTML;
                                                document.body.innerHTML = `
                                                    <div style="max-width: 800px; margin: 0 auto; padding: 20px; font-family: sans-serif; line-height: 1.8; font-size: 19px; color: #333; background-color: #fdf6e3;">
                                                        ${'$'}{content}
                                                    </div>
                                                `;
                                                document.querySelectorAll('img').forEach(img => {
                                                    img.style.maxWidth = '100%';
                                                    img.style.height = 'auto';
                                                });
                                                return true;
                                            } else {
                                                return false;
                                            }
                                        })();
                                    """.trimIndent()
                                    webView?.evaluateJavascript(js) { result ->
                                        if (result == "true") {
                                            isReaderMode = true
                                        } else {
                                            Toast.makeText(context, context.getString(R.string.no_content_detected), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.copy_link)) },
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("link", webView?.url ?: url)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.open_in_browser)) },
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webView?.url ?: url))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, context.getString(R.string.open_failed), Toast.LENGTH_SHORT).show()
                                }
                                showMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AndroidView(
                factory = { ctx ->
                    // 从管理器中获取或创建一个预设的 WebView
                    WebViewManager.acquire(ctx).apply {
                        webView = this
                        
                        // 1. 优化 WebSettings 配置
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            
                            // 缓存策略：连网时使用默认策略，不连网时强制加载缓存
                            cacheMode = WebSettings.LOAD_DEFAULT
                            
                            // 混合内容模式：允许在 HTTPS 页面中加载 HTTP 内容
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            
                            // 资源预加载优化
                            loadsImagesAutomatically = true
                            
                            // 用户代理
                            userAgentString += " WanAndroidApp/1.0"
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                canGoBack = view?.canGoBack() == true
                                isReaderMode = false // 页面开始加载，重置阅读模式状态
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                canGoBack = view?.canGoBack() == true
                                
                                // 注入 JS 隐藏常见的 "APP内打开"、"打开APP" 按钮和悬浮窗
                                val js = """
                                    (function() {
                                        const clean = () => {
                                            const keywords = ['APP内打开', '打开APP', '下载APP', 'App内打开', '立即打开'];
                                            document.querySelectorAll('button, a, div, span').forEach(el => {
                                                if (el.children.length > 2) return;
                                                const text = el.innerText || '';
                                                if (keywords.some(k => text.includes(k)) && text.length < 20) {
                                                    el.style.setProperty('display', 'none', 'important');
                                                }
                                            });
                                        };
                                        clean();
                                        setTimeout(clean, 1000);
                                        new MutationObserver(clean).observe(document.body, { childList: true, subtree: true });
                                    })();
                                """.trimIndent()
                                view?.evaluateJavascript(js, null)
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                val urlStr = request?.url?.toString() ?: return false
                                if (urlStr.startsWith("http://") || urlStr.startsWith("https://")) {
                                    return false
                                }
                                return true // 拦截非 HTTP(S) 请求
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                if (!title.isNullOrBlank()) {
                                    currentTitle = title
                                }
                            }
                        }

                        loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    // 生命周期管理：当 Screen 销毁时，释放 WebView
    DisposableEffect(Unit) {
        onDispose {
            webView?.let { 
                WebViewManager.release(it)
                webView = null
            }
        }
    }
}
