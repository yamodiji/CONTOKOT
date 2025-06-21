package com.speedDrawer.speed_drawer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room

import com.speedDrawer.speed_drawer.data.local.dao.AppDao
import com.speedDrawer.speed_drawer.data.local.database.SpeedDrawerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// DataStore extension property
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideSpeedDrawerDatabase(@ApplicationContext context: Context): SpeedDrawerDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            SpeedDrawerDatabase::class.java,
            "speed_drawer_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    fun provideAppDao(database: SpeedDrawerDatabase): AppDao {
        return database.appDao()
    }
    
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }


} 