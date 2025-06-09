package com.swackles.jellyfin.data

import com.swackles.jellyfin.domain.auth.AuthContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object JellyfinModule {
    @Provides
    fun providerJellyfinRepository() =
        AuthContext.getJellyfinClient()
}
