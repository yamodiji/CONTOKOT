package com.speedDrawer.speed_drawer.data.model

import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "apps")
data class AppInfo(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val systemAppName: String? = null,
    val versionName: String? = null,
    val versionCode: Int? = null,
    val dataDir: String? = null,
    val isSystemApp: Boolean = false,
    val installTimeMillis: Long = 0L,
    val updateTimeMillis: Long = 0L,
    val category: String = "Unknown",
    val isEnabled: Boolean = true,
    
    // Performance tracking
    val launchCount: Int = 0,
    val lastLaunchTime: Long = 0L,
    val isFavorite: Boolean = false,
    val searchScore: Double = 0.0
) : Parcelable {
    
    // Transient properties (not stored in Room)
    @Transient
    var icon: Drawable? = null
    
    /**
     * Get display name (prefer app name over system name)
     */
    val displayName: String
        get() = if (appName.isNotEmpty()) appName else (systemAppName ?: packageName)
    
    /**
     * Check if this is a launcher app
     */
    val isLauncher: Boolean
        get() = packageName.contains("launcher") || 
                category.lowercase().contains("launcher")
    
    /**
     * Check if this is a system app that should be hidden
     */
    val shouldHide: Boolean
        get() = isSystemApp && 
                (packageName.startsWith("com.android.") ||
                 packageName.startsWith("com.google.android.") ||
                 packageName.contains("packageinstaller") ||
                 packageName.contains("wallpaper"))
    
    /**
     * Check if app matches search query
     */
    fun matchesQuery(query: String): Boolean {
        if (query.isEmpty()) return true
        
        val lowerQuery = query.lowercase()
        val lowerAppName = appName.lowercase()
        val lowerPackageName = packageName.lowercase()
        
        return lowerAppName.contains(lowerQuery) ||
               lowerPackageName.contains(lowerQuery) ||
               (systemAppName?.lowercase()?.contains(lowerQuery) ?: false)
    }
    
    /**
     * Create a copy with updated launch data
     */
    fun incrementLaunchCount(): AppInfo {
        return copy(
            launchCount = launchCount + 1,
            lastLaunchTime = System.currentTimeMillis()
        )
    }
    
    /**
     * Create a copy with updated favorite status
     */
    fun toggleFavorite(): AppInfo {
        return copy(isFavorite = !isFavorite)
    }
    
    companion object {
        /**
         * Packages to hide from the app list
         */
        val HIDDEN_PACKAGES = setOf(
            "com.android.launcher",
            "com.google.android.launcher",
            "com.sec.android.app.launcher",
            "com.miui.home",
            "com.oneplus.launcher",
            "com.android.settings",
            "com.android.packageinstaller"
        )
    }
} 