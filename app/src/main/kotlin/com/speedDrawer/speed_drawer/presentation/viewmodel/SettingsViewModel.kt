package com.speedDrawer.speed_drawer.presentation.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speedDrawer.speed_drawer.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    
    // DataStore keys
    private val themeKey = stringPreferencesKey(Constants.THEME_KEY)
    private val iconSizeKey = floatPreferencesKey(Constants.ICON_SIZE_KEY)
    private val backgroundOpacityKey = floatPreferencesKey(Constants.BACKGROUND_OPACITY_KEY)
    private val fuzzySearchKey = booleanPreferencesKey(Constants.FUZZY_SEARCH_KEY)
    private val showMostUsedKey = booleanPreferencesKey(Constants.SHOW_MOST_USED_KEY)
    private val autoFocusKey = booleanPreferencesKey(Constants.AUTO_FOCUS_KEY)
    private val vibrationKey = booleanPreferencesKey(Constants.VIBRATION_KEY)
    private val animationsKey = booleanPreferencesKey(Constants.ANIMATIONS_KEY)
    private val showKeyboardKey = booleanPreferencesKey(Constants.SHOW_KEYBOARD_KEY)
    private val showSearchHistoryKey = booleanPreferencesKey(Constants.SHOW_SEARCH_HISTORY_KEY)
    private val clearSearchOnCloseKey = booleanPreferencesKey(Constants.CLEAR_SEARCH_ON_CLOSE_KEY)
    
    // Settings state flows
    val themeMode: StateFlow<Constants.ThemeMode> = dataStore.data
        .map { preferences ->
            val themeName = preferences[themeKey] ?: Constants.ThemeMode.SYSTEM.name
            try {
                Constants.ThemeMode.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                Constants.ThemeMode.SYSTEM
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Constants.ThemeMode.SYSTEM
        )
    
    val iconSize: StateFlow<Float> = dataStore.data
        .map { preferences ->
            preferences[iconSizeKey] ?: Constants.MEDIUM_ICON_SIZE.value
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Constants.MEDIUM_ICON_SIZE.value
        )
    
    val backgroundOpacity: StateFlow<Float> = dataStore.data
        .map { preferences ->
            preferences[backgroundOpacityKey] ?: 0.9f
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.9f
        )
    
    val isFuzzySearchEnabled: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[fuzzySearchKey] ?: true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    
    val showMostUsed: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[showMostUsedKey] ?: true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    
    val autoFocus: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[autoFocusKey] ?: true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    
    val isVibrationEnabled: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[vibrationKey] ?: true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    
    val areAnimationsEnabled: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[animationsKey] ?: true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    
    val showKeyboard: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[showKeyboardKey] ?: true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    
    val showSearchHistory: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[showSearchHistoryKey] ?: true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    
    val clearSearchOnClose: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[clearSearchOnCloseKey] ?: false
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    // Update methods
    fun setThemeMode(themeMode: Constants.ThemeMode) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[themeKey] = themeMode.name
            }
        }
    }
    
    fun setIconSize(size: Float) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[iconSizeKey] = size
            }
        }
    }
    
    fun setBackgroundOpacity(opacity: Float) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[backgroundOpacityKey] = opacity
            }
        }
    }
    
    fun setFuzzySearchEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[fuzzySearchKey] = enabled
            }
        }
    }
    
    fun setShowMostUsed(show: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[showMostUsedKey] = show
            }
        }
    }
    
    fun setAutoFocus(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[autoFocusKey] = enabled
            }
        }
    }
    
    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[vibrationKey] = enabled
            }
        }
    }
    
    fun setAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[animationsKey] = enabled
            }
        }
    }
    
    fun setShowKeyboard(show: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[showKeyboardKey] = show
            }
        }
    }
    
    fun setShowSearchHistory(show: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[showSearchHistoryKey] = show
            }
        }
    }
    
    fun setClearSearchOnClose(clear: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[clearSearchOnCloseKey] = clear
            }
        }
    }
    
    fun toggleTheme() {
        viewModelScope.launch {
            val currentTheme = themeMode.value
            val newTheme = when (currentTheme) {
                Constants.ThemeMode.LIGHT -> Constants.ThemeMode.DARK
                Constants.ThemeMode.DARK -> Constants.ThemeMode.SYSTEM
                Constants.ThemeMode.SYSTEM -> Constants.ThemeMode.LIGHT
            }
            setThemeMode(newTheme)
        }
    }
} 