package com.swackles.jellyfin.domain.repository

import com.swackles.jellyfin.domain.models.DetailMedia
import com.swackles.jellyfin.domain.models.MediaSection
import org.jellyfin.sdk.model.UUID

interface MediaRepository {
    suspend fun getMedia(id: UUID): DetailMedia
    suspend fun getContinueWatching(): MediaSection
    suspend fun getNewlyAdded(): MediaSection
    suspend fun getRecommended(): MediaSection
}