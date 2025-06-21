package com.speedDrawer.speed_drawer.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.speedDrawer.speed_drawer.data.local.dao.AppDao
import com.speedDrawer.speed_drawer.data.model.AppInfo

@Database(
    entities = [AppInfo::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SpeedDrawerDatabase : RoomDatabase() {
    
    abstract fun appDao(): AppDao
    
    companion object {
        @Volatile
        private var INSTANCE: SpeedDrawerDatabase? = null
        
        fun getDatabase(context: Context): SpeedDrawerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpeedDrawerDatabase::class.java,
                    "speed_drawer_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 