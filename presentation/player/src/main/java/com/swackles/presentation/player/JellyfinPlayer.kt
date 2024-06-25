package com.swackles.presentation.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.swackles.jellyfin.data.jellyfin.repository.VideoMetadataReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(UnstableApi::class)
class JellyfinPlayer(
    private val context: Context,
    private val metadataReader: VideoMetadataReader
) {
    val player: ExoPlayer

    init {
        val default = DefaultMediaSourceFactory(
            DefaultDataSource.Factory(
                context,
                JellyfinHTTPDataSource(AUTH_TOKEN, DEVICE_ID)
            )
        )
        player = ExoPlayer.Builder(context, default).build()
        player.playWhenReady = true
    }

    fun release() {
        player.release()
    }

    fun addMedia(id: UUID) {
        CoroutineScope(Dispatchers.Main).launch {
            val metaData = metadataReader.getMetadataUsingId(id)

            val uri = Uri.Builder()
                .scheme(metaData.scheme)
                .authority(metaData.host)
                .path("/Videos/$id/master.m3u8")
                .appendQueryParameter(MEDIA_SOURCE_ID_KEY, metaData.mediaSourceId)
                .appendQueryParameter(MEDIA_DEVICE_ID_KEY, DEVICE_ID)
                .appendQueryParameter(MEDIA_VIDEO_CODEC_KEY, MEDIA_VIDEO_CODEC_VALUE)
                .appendQueryParameter(MEDIA_PLAY_SESSION_ID_KEY, PLAY_SESSION_ID)
                .build()

            val mediaItem = MediaItem.Builder()
                .setMimeType(MimeTypes.APPLICATION_M3U8)
                .setUri(uri)
                .build()

            player.setMediaItem(mediaItem)
            player.prepare()
        }
    }

    // TODO: Temporary, Remove before committing
    private companion object {
        const val PLAY_SESSION_ID = ""
        const val DEVICE_ID = ""
        const val AUTH_TOKEN = ""

        const val MEDIA_SOURCE_ID_KEY = "mediaSourceId"
        const val MEDIA_DEVICE_ID_KEY = "DeviceId"
        const val MEDIA_VIDEO_CODEC_KEY = "VideoCodec"
        const val MEDIA_VIDEO_CODEC_VALUE = "h264,h264"
        const val MEDIA_PLAY_SESSION_ID_KEY = "PlaySessionId"
    }
}
