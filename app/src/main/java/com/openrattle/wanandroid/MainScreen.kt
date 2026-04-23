package com.openrattle.wanandroid

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.openrattle.wanandroid.home.HomeScreen
import com.openrattle.wanandroid.mine.MineScreen
import com.openrattle.wanandroid.navi.NaviScreen
import com.openrattle.wanandroid.plaza.PlazaScreen
import com.openrattle.wanandroid.qa.QaScreen

sealed class Screen(val route: String, val labelRes: Int, val icon: ImageVector) {
    object Home : Screen("home", R.string.nav_home, Icons.Default.Home)
    object Plaza : Screen("plaza", R.string.nav_plaza, Icons.Default.Dashboard)
    object QA : Screen("qa", R.string.nav_qa, Icons.Outlined.QuestionAnswer)
    object System : Screen("system", R.string.nav_system, Icons.AutoMirrored.Filled.MenuBook)
    object Mine : Screen("mine", R.string.nav_mine, Icons.Default.Person)
}

val items = listOf(
    Screen.Home,
    Screen.QA,
    Screen.Plaza,
    Screen.System,
    Screen.Mine
)

@Composable
fun MainScreen(
    onNavigateToUrl: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCollect: () -> Unit,
    onNavigateToShare: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToMessage: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val navController = rememberNavController()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background, // 显式设置背景色，防止转场时透明
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Surface(
                color = NavigationBarDefaults.containerColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                NavigationBar(
                    modifier = Modifier.navigationBarsPadding(),
                    containerColor = Color.Transparent,
                    windowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(stringResource(screen.labelRes)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
        ) {
            composable(Screen.Home.route) { 
                HomeScreen(
                    onArticleClick = onNavigateToUrl,
                    onSearchClick = onNavigateToSearch,
                    onNavigateToLogin = onNavigateToLogin,
                    onMessageClick = onNavigateToMessage
                )
            }
            composable(Screen.Plaza.route) { 
                PlazaScreen(
                    onArticleClick = onNavigateToUrl,
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToShare = onNavigateToShare
                )
            }
            composable(Screen.QA.route) { 
                QaScreen(
                    onArticleClick = onNavigateToUrl,
                    onNavigateToLogin = onNavigateToLogin
                )
            }
            composable(Screen.System.route) { 
                NaviScreen(onArticleClick = onNavigateToUrl)
            }
            composable(Screen.Mine.route) { 
                MineScreen(
                    onNavigateToUrl = onNavigateToUrl,
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToCollect = onNavigateToCollect,
                    onNavigateToHistory = onNavigateToHistory,
                    onNavigateToSettings = onNavigateToSettings
                )
            }
        }
    }
}
