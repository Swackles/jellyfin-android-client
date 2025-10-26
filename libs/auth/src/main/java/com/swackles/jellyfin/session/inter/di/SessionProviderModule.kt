package com.swackles.jellyfin.session.inter.di

import android.content.Context
import com.swackles.jellyfin.session.SessionManager
import com.swackles.jellyfin.session.SessionStorage
import com.swackles.jellyfin.session.inter.SessionManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SessionProviderModule {
    @Provides
    @Singleton
    fun provideSessionManager(
        @ApplicationContext context: Context,
        storage: SessionStorage
    ): SessionManager =
        SessionManagerImpl(context, storage)
}
