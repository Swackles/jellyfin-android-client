package com.swackles.jellyfin.di

import android.content.Context
import androidx.room.Room
import com.swackles.jellyfin.data.JellyfinDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesJellyfinDatabase(@ApplicationContext context: Context): JellyfinDatabase =
        Room.databaseBuilder(
            context = context,
            klass = JellyfinDatabase::class.java,
            name = "jellyfin-db"
        ).build()

    @Provides
    @Singleton
    fun providesSessionDao(database: JellyfinDatabase) = database.sessionDao()
}