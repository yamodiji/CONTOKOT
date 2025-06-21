package com.speedDrawer.speed_drawer.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.speedDrawer.speed_drawer.presentation.viewmodel.SettingsViewModel
import com.speedDrawer.speed_drawer.util.Constants

private val DarkColorScheme = darkColorScheme(
    primary = Constants.PRIMARY_COLOR,
    secondary = Constants.ACCENT_COLOR,
    tertiary = Constants.PRIMARY_DARK_COLOR,
    error = Constants.ERROR_COLOR
)

private val LightColorScheme = lightColorScheme(
    primary = Constants.PRIMARY_COLOR,
    secondary = Constants.ACCENT_COLOR,
    tertiary = Constants.PRIMARY_DARK_COLOR,
    error = Constants.ERROR_COLOR
)

@Composable
fun SpeedDrawerTheme(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val themeMode by settingsViewModel.themeMode.collectAsState()
    val backgroundOpacity by settingsViewModel.backgroundOpacity.collectAsState()
    
    val darkTheme = when (themeMode) {
        Constants.ThemeMode.DARK -> true
        Constants.ThemeMode.LIGHT -> false
        Constants.ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    
    val colorScheme = when {
        darkTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            dynamicDarkColorScheme(LocalContext.current)
        }
        !darkTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            dynamicLightColorScheme(LocalContext.current)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }.copy(
        // Apply background opacity
        background = if (darkTheme) {
            androidx.compose.ui.graphics.Color.Black.copy(alpha = backgroundOpacity)
        } else {
            androidx.compose.ui.graphics.Color.White.copy(alpha = backgroundOpacity)
        }
    )
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = androidx.compose.ui.graphics.Color.Transparent.toArgb()
            window.navigationBarColor = androidx.compose.ui.graphics.Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
} 