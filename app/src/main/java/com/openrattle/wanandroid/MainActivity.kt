package com.openrattle.wanandroid

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.openrattle.base.model.ThemeMode
import com.openrattle.wanandroid.auth.LoginScreen
import com.openrattle.wanandroid.auth.RegisterScreen
import com.openrattle.wanandroid.collect.CollectScreen
import com.openrattle.wanandroid.message.MessageScreen
import com.openrattle.wanandroid.search.SearchScreen
import com.openrattle.common_ui.theme.WanAndroidTheme
import com.openrattle.core.utils.SettingsManager
import com.openrattle.wanandroid.history.HistoryScreen
import com.openrattle.wanandroid.plaza.ShareArticleScreen
import com.openrattle.wanandroid.settings.SettingsScreen
import com.openrattle.wanandroid.web.WebViewScreen
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        super.onCreate(savedInstanceState)
        setContent {
            val themeMode by settingsManager.themeMode.collectAsState(initial = ThemeMode.FOLLOW_SYSTEM)
            val isEyeProtection by settingsManager.isEyeProtectionEnabled.collectAsState(initial = false)
            
            val darkTheme = when (themeMode) {
                ThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            WanAndroidTheme(
                darkTheme = darkTheme,
                eyeProtection = isEyeProtection
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val rootNavController = rememberNavController()
                    NavHost(
                        navController = rootNavController,
                        startDestination = "main",
                        modifier = Modifier.fillMaxSize(),
                        // 设置更稳定的全局页面切换动画
                        enterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(300)
                            ) + fadeIn(animationSpec = tween(200)) // 稍微快一点淡入
                        },
                        exitTransition = {
                            // 关键优化：退出时不要完全淡出，或者通过缩放保持存在感，减少闪烁感
                            slideOutHorizontally(
                                targetOffsetX = { -it / 3 }, // 仅向左滑出 1/3 宽度
                                animationSpec = tween(300)
                            ) + fadeOut(animationSpec = tween(300), targetAlpha = 0.5f) // 不要完全透明
                        },
                        popEnterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { -it / 3 },
                                animationSpec = tween(300)
                            ) + fadeIn(animationSpec = tween(300))
                        },
                        popExitTransition = {
                            slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(300)
                            ) + fadeOut(animationSpec = tween(200))
                        }
                    ) {
                        composable(
                            "main",
                            popEnterTransition = {
                                EnterTransition.None
                            }
                        ) {
                            MainScreen(
                                onNavigateToUrl = { url ->
                                    val encodedUrl =
                                        URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    rootNavController.navigate("web/$encodedUrl")
                                },
                                onNavigateToLogin = {
                                    rootNavController.navigate("login")
                                },
                                onNavigateToSearch = {
                                    rootNavController.navigate("search")
                                },
                                onNavigateToCollect = {
                                    rootNavController.navigate("collect")
                                },
                                onNavigateToShare = {
                                    rootNavController.navigate("share")
                                },
                                onNavigateToHistory = {
                                    rootNavController.navigate("history")
                                },
                                onNavigateToMessage = {
                                    rootNavController.navigate("message")
                                },
                                onNavigateToSettings = {
                                    rootNavController.navigate("settings")
                                }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(onBack = { rootNavController.popBackStack() })
                        }
                        composable("message") {
                            MessageScreen(
                                onBack = { rootNavController.popBackStack() },
                                onNavigateToUrl = { url ->
                                    val encodedUrl =
                                        URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    rootNavController.navigate("web/$encodedUrl")
                                }
                            )
                        }
                        composable("history") {
                            HistoryScreen(
                                onBack = { rootNavController.popBackStack() },
                                onArticleClick = { url ->
                                    val encodedUrl =
                                        URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    rootNavController.navigate("web/$encodedUrl")
                                },
                                onNavigateToLogin = {
                                    rootNavController.navigate("login")
                                }
                            )
                        }
                        composable("share") {
                            ShareArticleScreen(
                                onBack = { rootNavController.popBackStack() },
                                onNavigateToLogin = {
                                    rootNavController.navigate("login")
                                }
                            )
                        }
                        composable(
                            "web/{url}",
                            arguments = listOf(navArgument("url") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val url = backStackEntry.arguments?.getString("url") ?: ""
                            WebViewScreen(
                                url = url,
                                title = stringResource(R.string.web_details),
                                onBack = { rootNavController.popBackStack() }
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                onBack = { rootNavController.popBackStack() },
                                onLoginSuccess = {
                                    rootNavController.popBackStack("main", inclusive = false)
                                },
                                onNavigateToRegister = {
                                    rootNavController.navigate("register")
                                }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                onBack = { rootNavController.popBackStack() },
                                onRegisterSuccess = {
                                    rootNavController.popBackStack("main", inclusive = false)
                                }
                            )
                        }
                        composable(
                            "search",
                            enterTransition = {
                                slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(400)
                                ) + fadeIn(animationSpec = tween(300))
                            },
                            exitTransition = {
                                slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween(400)
                                ) + fadeOut(animationSpec = tween(300))
                            },
                            popEnterTransition = {
                                fadeIn(animationSpec = tween(300))
                            },
                            popExitTransition = {
                                slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween(400)
                                )
                            }
                        ) {
                            SearchScreen(
                                onBack = { rootNavController.popBackStack() },
                                onArticleClick = { url ->
                                    val encodedUrl =
                                        URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    rootNavController.navigate("web/$encodedUrl")
                                },
                                onNavigateToLogin = {
                                    rootNavController.navigate("login")
                                }
                            )
                        }
                        composable("collect") {
                            CollectScreen(
                                onBack = { rootNavController.popBackStack() },
                                onArticleClick = { url ->
                                    val encodedUrl =
                                        URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    rootNavController.navigate("web/$encodedUrl")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
