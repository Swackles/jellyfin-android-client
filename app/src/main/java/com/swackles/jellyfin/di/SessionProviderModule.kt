package com.swackles.jellyfin.di

import com.swackles.jellyfin.data.SessionStorageImpl
import com.swackles.jellyfin.data.dao.ServerDao
import com.swackles.jellyfin.data.dao.SessionDao
import com.swackles.jellyfin.session.SessionStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SessionProviderModule {
    @Provides
    @Singleton
    fun provideSessionStorage(sessionDao: SessionDao, serverDao: ServerDao): SessionStorage =
        SessionStorageImpl(
            sessionDao = sessionDao,
            serverDao = serverDao
        )
}