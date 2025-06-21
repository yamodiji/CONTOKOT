package com.speedDrawer.speed_drawer.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speedDrawer.speed_drawer.data.local.dao.AppDao
import com.speedDrawer.speed_drawer.data.model.AppInfo
import com.speedDrawer.speed_drawer.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.xdrop.fuzzywuzzy.FuzzySearch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class AppViewModel @Inject constructor(
    private val appDao: AppDao,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    // Private mutable state
    private val _isLoading = MutableStateFlow(false)
    private val _searchQuery = MutableStateFlow("")
    private val _allApps = MutableStateFlow<List<AppInfo>>(emptyList())
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    private val _errorMessage = MutableStateFlow<String?>(null)
    
    // Public read-only state
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    val allApps: StateFlow<List<AppInfo>> = _allApps.asStateFlow()
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Derived state
    val favoriteApps: StateFlow<List<AppInfo>> = _allApps
        .map { apps -> apps.filter { it.isFavorite } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    val mostUsedApps: StateFlow<List<AppInfo>> = _allApps
        .map { apps -> 
            apps.sortedByDescending { it.launchCount }
                .take(Constants.MAX_MOST_USED_APPS)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    // Debounced search results
    val displayApps: StateFlow<List<AppInfo>> = _searchQuery
        .debounce(Constants.DEBOUNCE_DELAY_MS)
        .combine(_allApps) { query, apps ->
            if (query.isEmpty()) {
                // Show all apps sorted by favorites first, then launch count, then name
                apps.sortedWith(
                    compareByDescending<AppInfo> { it.isFavorite }
                        .thenByDescending { it.launchCount }
                        .thenBy { it.displayName.lowercase() }
                )
            } else {
                // Perform fuzzy search
                searchApps(query, apps)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    init {
        loadInstalledApps()
    }
    
    /**
     * Load all installed apps from system
     */
    fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val packageManager = context.packageManager
                val intent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                
                val resolveInfos = packageManager.queryIntentActivities(
                    intent, 
                    PackageManager.MATCH_DEFAULT_ONLY
                )
                
                val apps = resolveInfos.mapNotNull { resolveInfo ->
                    try {
                        createAppInfo(resolveInfo, packageManager)
                    } catch (e: Exception) {
                        null // Skip apps that can't be processed
                    }
                }.filter { app ->
                    // Filter out hidden packages and system apps
                    !AppInfo.HIDDEN_PACKAGES.any { hiddenPkg -> 
                        app.packageName.contains(hiddenPkg) 
                    } && !app.shouldHide
                }
                
                // Save to database
                appDao.insertApps(apps)
                _allApps.value = apps
                
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load apps: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Create AppInfo from ResolveInfo
     */
    private fun createAppInfo(
        resolveInfo: ResolveInfo,
        packageManager: PackageManager
    ): AppInfo {
        val packageInfo = packageManager.getPackageInfo(
            resolveInfo.activityInfo.packageName, 
            0
        )
        
        return AppInfo(
            packageName = resolveInfo.activityInfo.packageName,
            appName = resolveInfo.loadLabel(packageManager).toString(),
            systemAppName = resolveInfo.activityInfo.name,
            versionName = packageInfo.versionName,
            versionCode = packageInfo.longVersionCode.toInt(),
            installTimeMillis = packageInfo.firstInstallTime,
            updateTimeMillis = packageInfo.lastUpdateTime,
            isSystemApp = (resolveInfo.activityInfo.applicationInfo.flags and 
                          android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
        ).apply {
            // Load icon separately to avoid blocking
            icon = try {
                resolveInfo.loadIcon(packageManager)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * Search apps using fuzzy matching
     */
    private fun searchApps(query: String, apps: List<AppInfo>): List<AppInfo> {
        if (query.length < Constants.MIN_SEARCH_LENGTH) return apps
        
        return apps.mapNotNull { app ->
            val appNameScore = FuzzySearch.ratio(query.lowercase(), app.appName.lowercase())
            val packageNameScore = FuzzySearch.ratio(query.lowercase(), app.packageName.lowercase())
            val systemNameScore = app.systemAppName?.let { 
                FuzzySearch.ratio(query.lowercase(), it.lowercase()) 
            } ?: 0
            
            val maxScore = maxOf(appNameScore, packageNameScore, systemNameScore)
            
            if (maxScore >= (Constants.SEARCH_THRESHOLD * 100)) {
                app.copy(searchScore = maxScore.toDouble())
            } else {
                null
            }
        }.sortedByDescending { it.searchScore }
         .take(Constants.MAX_SEARCH_RESULTS)
    }
    
    /**
     * Update search query
     */
    fun search(query: String) {
        _searchQuery.value = query
        
        // Add to search history if not empty and not already present
        if (query.isNotEmpty() && !_searchHistory.value.contains(query)) {
            val newHistory = (_searchHistory.value + query)
                .takeLast(Constants.MAX_SEARCH_HISTORY)
            _searchHistory.value = newHistory
        }
    }
    
    /**
     * Clear search query
     */
    fun clearSearch() {
        _searchQuery.value = ""
    }
    
    /**
     * Launch an app
     */
    suspend fun launchApp(app: AppInfo): Boolean {
        return try {
            val packageManager = context.packageManager
            val launchIntent = packageManager.getLaunchIntentForPackage(app.packageName)
            
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                
                // Update launch count in database
                appDao.incrementLaunchCount(app.packageName)
                
                // Update local state
                val updatedApps = _allApps.value.map { existingApp ->
                    if (existingApp.packageName == app.packageName) {
                        existingApp.incrementLaunchCount()
                    } else {
                        existingApp
                    }
                }
                _allApps.value = updatedApps
                
                true
            } else {
                false
            }
        } catch (e: Exception) {
            _errorMessage.value = "Failed to launch ${app.displayName}: ${e.message}"
            false
        }
    }
    
    /**
     * Toggle favorite status of an app
     */
    fun toggleFavorite(app: AppInfo) {
        viewModelScope.launch {
            try {
                val newFavoriteStatus = !app.isFavorite
                appDao.updateFavoriteStatus(app.packageName, newFavoriteStatus)
                
                // Update local state
                val updatedApps = _allApps.value.map { existingApp ->
                    if (existingApp.packageName == app.packageName) {
                        existingApp.copy(isFavorite = newFavoriteStatus)
                    } else {
                        existingApp
                    }
                }
                _allApps.value = updatedApps
                
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update favorite: ${e.message}"
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Refresh apps list
     */
    fun refreshApps() {
        loadInstalledApps()
    }
} 