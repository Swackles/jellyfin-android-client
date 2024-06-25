package com.swackles.jellyfin.data.jellyfin.models

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

    private fun getMediaSource() = metadata.mediaSources.first()
}