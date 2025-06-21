package com.speedDrawer.speed_drawer.data.local.dao

import androidx.room.*
import com.speedDrawer.speed_drawer.data.model.AppInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    
    @Query("SELECT * FROM apps ORDER BY launchCount DESC, appName ASC")
    fun getAllApps(): Flow<List<AppInfo>>
    
    @Query("SELECT * FROM apps WHERE isFavorite = 1 ORDER BY appName ASC")
    fun getFavoriteApps(): Flow<List<AppInfo>>
    
    @Query("SELECT * FROM apps ORDER BY launchCount DESC LIMIT :limit")
    fun getMostUsedApps(limit: Int = 10): Flow<List<AppInfo>>
    
    @Query("SELECT * FROM apps WHERE packageName = :packageName")
    suspend fun getAppByPackage(packageName: String): AppInfo?
    
    @Query("SELECT * FROM apps WHERE appName LIKE '%' || :query || '%' OR packageName LIKE '%' || :query || '%' ORDER BY launchCount DESC")
    fun searchApps(query: String): Flow<List<AppInfo>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: AppInfo)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<AppInfo>)
    
    @Update
    suspend fun updateApp(app: AppInfo)
    
    @Delete
    suspend fun deleteApp(app: AppInfo)
    
    @Query("DELETE FROM apps WHERE packageName = :packageName")
    suspend fun deleteAppByPackage(packageName: String)
    
    @Query("DELETE FROM apps")
    suspend fun deleteAllApps()
    
    @Query("UPDATE apps SET launchCount = launchCount + 1, lastLaunchTime = :timestamp WHERE packageName = :packageName")
    suspend fun incrementLaunchCount(packageName: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE apps SET isFavorite = :isFavorite WHERE packageName = :packageName")
    suspend fun updateFavoriteStatus(packageName: String, isFavorite: Boolean)
    
    @Query("SELECT COUNT(*) FROM apps")
    suspend fun getAppCount(): Int
    
    @Query("SELECT * FROM apps WHERE packageName IN (:packageNames)")
    suspend fun getAppsByPackages(packageNames: List<String>): List<AppInfo>
} 