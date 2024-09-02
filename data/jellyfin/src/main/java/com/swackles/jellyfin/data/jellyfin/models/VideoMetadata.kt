package com.swackles.jellyfin.data.jellyfin.models

import org.jellyfin.sdk.model.api.MediaStreamType
import org.jellyfin.sdk.model.api.PlaybackInfoResponse
import java.util.UUID

class VideoMetadata(
    private val id: UUID,
    private val metadata: PlaybackInfoResponse,
    baseUrl: String
) {
    val scheme = "https"

    val host = baseUrl.replace("https://", "")

    val mediaSourceId = getMediaSource().id

    val playSessionId = metadata.playSessionId

    fun getSubtitles() = getMediaOfType(MediaStreamType.SUBTITLE)?.map { VideoSubtitleStream(
        id = it.index.toString(),
        label = it.displayTitle ?: "",
        language = it.language ?: "",
        url = it.deliveryUrl ?: "",
        isDefault = it.isDefault
    ) } ?: emptyList()

    private fun getMediaOfType(type: MediaStreamType) =
        getMediaSource().mediaStreams?.filter { it.type == type }

    private fun getMediaSource() = metadata.mediaSources.first()
}