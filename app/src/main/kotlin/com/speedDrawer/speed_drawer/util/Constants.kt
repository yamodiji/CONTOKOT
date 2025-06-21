package com.speedDrawer.speed_drawer.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object Constants {
    
    // DataStore keys
    const val THEME_KEY = "theme_mode"
    const val ICON_SIZE_KEY = "icon_size"
    const val BACKGROUND_OPACITY_KEY = "background_opacity"
    const val FUZZY_SEARCH_KEY = "fuzzy_search"
    const val SHOW_MOST_USED_KEY = "show_most_used"
    const val AUTO_FOCUS_KEY = "auto_focus"
    const val VIBRATION_KEY = "vibration"
    const val ANIMATIONS_KEY = "animations"
    const val SHOW_KEYBOARD_KEY = "show_keyboard"
    const val SHOW_SEARCH_HISTORY_KEY = "show_search_history"
    const val CLEAR_SEARCH_ON_CLOSE_KEY = "clear_search_on_close"
    const val SEARCH_HISTORY_KEY = "search_history"
    
    // Performance constants
    const val MAX_SEARCH_RESULTS = 50
    const val MAX_SEARCH_HISTORY = 20
    const val MAX_MOST_USED_APPS = 10
    const val DEBOUNCE_DELAY_MS = 100L
    const val ANIMATION_DURATION_MS = 150
    
    // Icon sizes (dp)
    val SMALL_ICON_SIZE = 32.dp
    val MEDIUM_ICON_SIZE = 48.dp
    val LARGE_ICON_SIZE = 64.dp
    val EXTRA_LARGE_ICON_SIZE = 80.dp
    
    // Spacing (dp)
    val PADDING_SMALL = 8.dp
    val PADDING_MEDIUM = 16.dp
    val PADDING_LARGE = 24.dp
    val BORDER_RADIUS = 12.dp
    
    // Colors
    val PRIMARY_COLOR = Color(0xFF2196F3)
    val PRIMARY_DARK_COLOR = Color(0xFF1976D2)
    val ACCENT_COLOR = Color(0xFF03DAC6)
    val ERROR_COLOR = Color(0xFFB00020)
    
    // Search configuration
    const val SEARCH_THRESHOLD = 0.6
    const val MIN_SEARCH_LENGTH = 1
    
    // Theme modes
    enum class ThemeMode {
        LIGHT, DARK, SYSTEM
    }
    
    // Quick actions
    const val QUICK_ACTION_SEARCH = "search"
    const val QUICK_ACTION_FAVORITES = "favorites"
    const val QUICK_ACTION_SETTINGS = "settings"
} 