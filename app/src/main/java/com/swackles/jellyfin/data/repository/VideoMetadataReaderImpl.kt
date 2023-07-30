package com.swackles.jellyfin.data.repository

import com.swackles.jellyfin.data.models.VideoMetadata
import java.util.UUID
import javax.inject.Inject

class VideoMetadataReaderImpl @Inject constructor(
    private val api: JellyfinRepository
): VideoMetadataReader {
    override suspend fun getMetadataUsingId(id: UUID): VideoMetadata =
        VideoMetadata(id, api.getMetadata(id), api.getBaseUrl())
}