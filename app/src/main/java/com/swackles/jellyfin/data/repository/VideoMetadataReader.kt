package com.swackles.jellyfin.data.repository

import com.swackles.jellyfin.data.models.VideoMetadata
import java.util.UUID

interface VideoMetadataReader {
    suspend fun getMetadataUsingId(id: UUID): VideoMetadata
}