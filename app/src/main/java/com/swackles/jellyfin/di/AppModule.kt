package com.swackles.jellyfin.di

import android.content.Context
import com.swackles.jellyfin.data.repository.JellyfinRepository
import com.swackles.jellyfin.data.repository.JellyfinRepositoryImpl
import com.swackles.jellyfin.domain.repository.MediaRepository
import com.swackles.jellyfin.domain.repository.MediaRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.createJellyfin
import org.jellyfin.sdk.model.ClientInfo
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJellyfinApiClient(@ApplicationContext appContext: Context): ApiClient {
        return createJellyfin {
            context = appContext
            clientInfo = ClientInfo(
                name = "Jellyfin WIP app",
                version = "0.1-ALPHA"
            )
        }.createApi(DevConfiguration.url)
    }

    @Provides
    @Singleton
    fun providerJellyfinRepository(jellyfinClient: ApiClient): JellyfinRepository {
        return JellyfinRepositoryImpl(jellyfinClient)
    }

    @Provides
    @Singleton
    fun provideMediaRepository(api: JellyfinRepository): MediaRepository {
        return MediaRepositoryImpl(api)
    }
}

private object DevConfiguration {
    // TODO: Remove this before commit
    const val url = "http://82.131.84.172:8096"
}