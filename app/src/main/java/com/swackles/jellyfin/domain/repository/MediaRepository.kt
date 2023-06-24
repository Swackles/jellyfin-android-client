package com.swackles.jellyfin.domain.repository

import com.swackles.jellyfin.domain.models.MediaSection

interface MediaRepository {
    suspend fun getContinueWatching(): MediaSection
    suspend fun getNewlyAdded(): MediaSection
    suspend fun getRecommended(): MediaSection
}