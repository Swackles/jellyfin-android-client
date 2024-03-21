package com.swackles.jellyfin.data

import android.app.Application
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.swackles.jellyfin.data.jellyfin.repository.JellyfinRepository
import com.swackles.jellyfin.data.jellyfin.repository.VideoMetadataReader
import com.swackles.jellyfin.data.jellyfin.repository.VideoMetadataReaderImpl
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

        return ExoPlayer.Builder(app)
            .apply {
                setSeekBackIncrementMs(PLAYER_SEEK_INCREMENT)
                setSeekForwardIncrementMs(PLAYER_SEEK_INCREMENT)
            }
            .setMediaSourceFactory(DefaultMediaSourceFactory(datasource))
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideMetaDataReader(api: JellyfinRepository): VideoMetadataReader =
        VideoMetadataReaderImpl(api)
}

private const val PLAYER_SEEK_INCREMENT = 10 * 1000L // 10 seconds