package com.swackles.jellyfin.data.jellyfin

import com.swackles.jellyfin.data.jellyfin.models.VideoMetadata
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.extensions.mediaInfoApi
import java.util.UUID

class MediaServiceImpl(
    private val jellyfinClient: ApiClient
): MediaService {
    override suspend fun getMetadataUsingId(id: UUID): VideoMetadata =
        VideoMetadata(
            id = id,
            metadata = jellyfinClient.mediaInfoApi.getPlaybackInfo(id).content,
            baseUrl = jellyfinClient.baseUrl!!
        )
}