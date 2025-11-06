@file:OptIn(androidx.media3.common.util.UnstableApi::class)

package com.swackles.jellyfin.di


import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.swackles.libs.jellyfin.JellyfinClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object PlayerModule {
    @Provides
    @ViewModelScoped
    fun  provideVideoPlayer(@ApplicationContext context: Context, jellyfinClient: JellyfinClient): Player {
        val datasource = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(jellyfinClient.getHeaders())

        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(datasource))
            .build()
    }
}
