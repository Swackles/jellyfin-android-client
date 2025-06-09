package com.swackles.jellyfin.data.jellyfin

import com.swackles.jellyfin.data.jellyfin.models.VideoMetadata
import java.util.UUID

interface MediaService {
    suspend fun getMetadataUsingId(id: UUID): VideoMetadata
}