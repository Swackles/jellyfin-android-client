package com.swackles.jellyfin.data

import android.content.Context
import com.swackles.jellyfin.data.repository.JellyfinRepository
import com.swackles.jellyfin.data.repository.JellyfinRepositoryImpl
import com.swackles.jellyfin.data.database.ServerRoomDatabase
import com.swackles.jellyfin.data.repository.MediaRepository
import com.swackles.jellyfin.data.repository.MediaRepositoryImpl
import com.swackles.jellyfin.data.repository.ServerRepository
import com.swackles.jellyfin.data.repository.ServerRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.jellyfin.sdk.api.client.ApiClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideJellyfinRepository(@ApplicationContext context: Context): ApiClient {
        return JellyfinRepositoryImpl.getInstance(context)
    }

    @Provides
    fun providerJellyfinRepository(jellyfinClient: ApiClient,
                                   @ApplicationContext context: Context
    ): JellyfinRepository {
        return JellyfinRepositoryImpl(jellyfinClient, context)
    }

    @Provides
    fun provideMediaRepository(api: JellyfinRepository): MediaRepository {
        return MediaRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideServerRepository(@ApplicationContext context: Context): ServerRepository {
        return ServerRepositoryImpl(ServerRoomDatabase.getInstance(context).serverDao())
    }
}
