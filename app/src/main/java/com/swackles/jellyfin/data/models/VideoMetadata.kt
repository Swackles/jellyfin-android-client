package com.swackles.jellyfin.data.models

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import org.jellyfin.sdk.model.api.MediaSourceInfo
import org.jellyfin.sdk.model.api.MediaStream
import org.jellyfin.sdk.model.api.MediaStreamType
import org.jellyfin.sdk.model.api.PlaybackInfoResponse
import java.util.UUID

class VideoMetadata(
    private val id: UUID,
    private val metadata: PlaybackInfoResponse,
    private val baseUrl: String
) {
    fun getVideoItem(): VideoItem {
        val source = getMediaSource()

        return VideoItem(
            name = source.name ?: "",
            mediaItem = getMediaItem(source)
        )
    }

    fun getMediaItem(): MediaItem = getMediaItem(getMediaSource())

    fun getAudios(): List<MediaStreams> =
        getMediaStreamOfType(MediaStreamType.AUDIO).map { MediaStreams(
            title = "${it.language} [${it.channelLayout}]",
            index = it.index
        ) }

    fun getSubtitles(): List<MediaStreams> =
        getMediaStreamOfType(MediaStreamType.AUDIO).map { MediaStreams(
            title = "${it.language}",
            index = it.index
        ) }

    private fun getMediaStreamOfType(type: MediaStreamType): List<MediaStream> =
        metadata.mediaSources.first().mediaStreams?.filter { it.type == type } ?: emptyList()


    private fun getMediaItem(source: MediaSourceInfo): MediaItem {
        val url = Uri.Builder()
            .path("/Videos/$id/master.m3u8")
            .appendQueryParameter("mediaSourceId", source.id)
            .build()
            .toString()

        // val url = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8"//"$baseUr"
        println(url)
        println("fulluri: $baseUrl$url")

        return MediaItem.Builder()
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .setUri(baseUrl + url)
            .build()
    }


    private fun getMediaSource() = metadata.mediaSources.first()
}