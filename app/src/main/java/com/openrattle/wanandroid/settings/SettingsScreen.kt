package com.openrattle.wanandroid.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openrattle.base.model.ThemeMode
import com.openrattle.wanandroid.R
import com.openrattle.wanandroid.ui.components.NoRippleIconButton
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showThemeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SettingsEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.more_settings)) },
                navigationIcon = {
                    NoRippleIconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.dark_mode)) },
                    supportingContent = {
                        val currentModeDisplay = when (state.themeMode) {
                            ThemeMode.FOLLOW_SYSTEM -> "跟随系统"
                            ThemeMode.LIGHT -> "浅色模式"
                            ThemeMode.DARK -> "深色模式"
                        }
                        Text(currentModeDisplay)
                    },
                    leadingContent = {
                        val icon = when (state.themeMode) {
                            ThemeMode.FOLLOW_SYSTEM -> Icons.Default.SettingsBrightness
                            ThemeMode.LIGHT -> Icons.Default.LightMode
                            ThemeMode.DARK -> Icons.Default.DarkMode
                        }
                        Icon(icon, contentDescription = null)
                    },
                    modifier = Modifier.clickable { showThemeDialog = true }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.eye_protection)) },
                    leadingContent = {
                        Icon(Icons.Default.RemoveRedEye, contentDescription = null)
                    },
                    trailingContent = {
                        Switch(
                            checked = state.isEyeProtectionEnabled,
                            onCheckedChange = {
                                viewModel.dispatch(SettingsIntent.SetEyeProtectionEnabled(it))
                            },
                            thumbContent = {
                                Icon(
                                    imageVector = if (state.isEyeProtectionEnabled) Icons.Filled.Check else Icons.Filled.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            },
                            colors = SwitchDefaults.colors(
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                uncheckedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                )
            }
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("选择主题模式") },
            text = {
                Column {
                    ThemeOption(
                        title = "跟随系统",
                        icon = Icons.Default.SettingsBrightness,
                        selected = state.themeMode == ThemeMode.FOLLOW_SYSTEM,
                        onClick = {
                            viewModel.dispatch(SettingsIntent.SetThemeMode(ThemeMode.FOLLOW_SYSTEM))
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        title = "浅色模式",
                        icon = Icons.Default.LightMode,
                        selected = state.themeMode == ThemeMode.LIGHT,
                        onClick = {
                            viewModel.dispatch(SettingsIntent.SetThemeMode(ThemeMode.LIGHT))
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        title = "深色模式",
                        icon = Icons.Default.DarkMode,
                        selected = state.themeMode == ThemeMode.DARK,
                        onClick = {
                            viewModel.dispatch(SettingsIntent.SetThemeMode(ThemeMode.DARK))
                            showThemeDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun ThemeOption(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        RadioButton(selected = selected, onClick = onClick)
    }
}
