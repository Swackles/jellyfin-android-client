package com.swackles.jellyfin.session.inter.di

import android.content.Context
import com.swackles.libs.jellyfin.JellyfinClient
import com.swackles.libs.jellyfin.JellyfinClientImpl
import com.swackles.libs.jellyfin.JellyfinCredentials
import com.swackles.libs.jellyfin.LibraryClient
import com.swackles.libs.jellyfin.MediaClient
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class MutableJellyfinProviderHolder @Inject constructor() {
    var jellyfinClient: JellyfinClient? = null
}

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface JellyfinProviderEntryPoint {
    fun jellyfinProviderHolder(): MutableJellyfinProviderHolder
}

internal object JellyfinProviderFactory {
    suspend fun login(context: Context, credentials: JellyfinCredentials): JellyfinClient {
        val holder = EntryPoints.get(context, JellyfinProviderEntryPoint::class.java)
            .jellyfinProviderHolder()

        holder.jellyfinClient = JellyfinClientImpl.login(context, credentials)

        return holder.jellyfinClient!!
    }

    fun logOut(context: Context) {
        val holder = EntryPoints.get(context, JellyfinProviderEntryPoint::class.java)
            .jellyfinProviderHolder()

        holder.jellyfinClient = null
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal object JellyfinProviderModule {
    @Singleton
    @Provides
    fun providerJellyfinClient(holder: MutableJellyfinProviderHolder): JellyfinClient =
        holder.jellyfinClient ?: throw RuntimeException("Cannot use jellyfin client before setting it")

    @Singleton
    @Provides
    fun provideJellyfinLibraryClient(jellyfinClient: JellyfinClient): LibraryClient =
        jellyfinClient.libraryClient

    @Singleton
    @Provides
    fun provideJellyfinMediaClient(jellyfinClient: JellyfinClient): MediaClient =
        jellyfinClient.mediaClient
}