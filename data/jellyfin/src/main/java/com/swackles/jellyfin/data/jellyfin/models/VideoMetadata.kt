package com.swackles.jellyfin.data.jellyfin.models

import org.jellyfin.sdk.model.api.PlaybackInfoResponse
import java.util.UUID

data class VideoMetadata(
    val id: UUID,
    val metadata: PlaybackInfoResponse,
    val baseUrl: String
)