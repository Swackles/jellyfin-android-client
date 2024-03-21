package com.swackles.jellyfin.data.jellyfin.repository

import com.swackles.jellyfin.data.jellyfin.models.VideoMetadata
import java.util.UUID

interface VideoMetadataReader {
    suspend fun getMetadataUsingId(id: UUID): VideoMetadata
}