package com.swackles.presentation.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.SubtitleConfiguration
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.swackles.jellyfin.data.jellyfin.repository.VideoMetadataReader
import kotlinx.coroutines.runBlocking
import java.util.UUID

@OptIn(UnstableApi::class)
class JellyfinPlayer(
    context: Context,
    private val userContext: JellyfinPlayerUserContext,
    private val metadataReader: VideoMetadataReader
) {
    val player: ExoPlayer

    init {
        player = ExoPlayer.Builder(context).build()
        player.playWhenReady = true
    }

    fun release() {
        player.release()
    }

    fun addMedia(id: UUID) {
        runBlocking {
            val metaData = metadataReader.getMetadataUsingId(id)

            val videoUri = Uri.Builder()
                .scheme(metaData.scheme)
                .authority(metaData.host)
                .path("/Videos/$id/master.m3u8")
                .appendQueryParameter(MEDIA_SOURCE_ID_KEY, metaData.mediaSourceId)
                .appendQueryParameter(MEDIA_DEVICE_ID_KEY, userContext.deviceId)
                .appendQueryParameter(MEDIA_API_KEY_KEY, userContext.authToken)
                .appendQueryParameter(MEDIA_VIDEO_CODEC_KEY, MEDIA_VIDEO_CODEC_VALUE)
                .appendQueryParameter(MEDIA_PLAY_SESSION_ID_KEY, metaData.playSessionId)
                .build()

            val mediaItem = MediaItem.Builder()
                .setMimeType(MimeTypes.APPLICATION_M3U8)
                .setUri(videoUri)

            val subtitles = metaData.getSubtitles().mapIndexed { index, it ->
                val urlPathAndParams = it.url.split("?")

                val subtitleUri = Uri.Builder()
                    .scheme(metaData.scheme)
                    .authority(metaData.host)
                    .path(urlPathAndParams.first())
                    .appendQueryParameter(MEDIA_API_KEY_KEY, userContext.authToken)

                urlPathAndParams[1].split("&").forEach {
                    val keyAndValue = it.split("=")
                    subtitleUri.appendQueryParameter(keyAndValue.first(), keyAndValue[1])
                }

                val subtitleConfig = SubtitleConfiguration.Builder(subtitleUri.build())
                    .setMimeType(MimeTypes.TEXT_SSA)
                    .setId(it.id)
                    .setLabel(it.label)
                    .setLanguage(it.language)

                if (it.isDefault) subtitleConfig.setSelectionFlags(C.SELECTION_FLAG_DEFAULT)

                subtitleConfig.build()
            }

            mediaItem.setSubtitleConfigurations(subtitles)

            player.setMediaItem(mediaItem.build())
            player.prepare()
        }
    }

    private companion object {
        const val MEDIA_API_KEY_KEY = "api_key"
        const val MEDIA_SOURCE_ID_KEY = "mediaSourceId"
        const val MEDIA_DEVICE_ID_KEY = "DeviceId"
        const val MEDIA_VIDEO_CODEC_KEY = "VideoCodec"
        const val MEDIA_VIDEO_CODEC_VALUE = "h264,h264"
        const val MEDIA_PLAY_SESSION_ID_KEY = "PlaySessionId"
    }
}
