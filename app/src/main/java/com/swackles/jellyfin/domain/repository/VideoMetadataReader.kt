package com.swackles.jellyfin.domain.repository

import com.swackles.jellyfin.domain.models.VideoMetadata
import java.util.UUID

interface VideoMetadataReader {
    suspend fun getMetadataUsingId(id: UUID): VideoMetadata
}