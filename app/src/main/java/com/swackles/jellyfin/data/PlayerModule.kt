package com.swackles.jellyfin.data

import android.app.Application
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.swackles.jellyfin.data.repository.JellyfinRepository
import com.swackles.jellyfin.data.repository.VideoMetadataReader
import com.swackles.jellyfin.data.repository.VideoMetadataReaderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
object PlayerModule {

    @Provides
    @ViewModelScoped
    fun  provideVideoPlayer(app: Application, jellyfinRepository: JellyfinRepository): Player {
        val datasource = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(jellyfinRepository.getHeaders())

        println("===== provideVideoPlayer =====")
        println(jellyfinRepository.getHeaders())

        return ExoPlayer.Builder(app)
            .setMediaSourceFactory(DefaultMediaSourceFactory(datasource))
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideMetaDataReader(api: JellyfinRepository): VideoMetadataReader =
        VideoMetadataReaderImpl(api)
}