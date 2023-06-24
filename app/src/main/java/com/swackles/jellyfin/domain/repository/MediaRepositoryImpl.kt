package com.swackles.jellyfin.domain.repository

import com.swackles.jellyfin.data.repository.JellyfinRepository
import com.swackles.jellyfin.domain.models.MediaSection
import com.swackles.jellyfin.domain.models.toMedia
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val api: JellyfinRepository
) : MediaRepository {
    override suspend fun getContinueWatching(): MediaSection {
        return MediaSection(
            "Continue watching",
            api.getContinueWatching().map { item -> item.toMedia(api.getBaseUrl()) }
        )
    }

    override suspend fun getNewlyAdded(): MediaSection {
        return MediaSection(
            "Newly added",
            api.getNewlyAdded().map { item -> item.toMedia(api.getBaseUrl()) }
        )
    }

    override suspend fun getRecommended(): MediaSection {
        return MediaSection(
            "My favorites",
            api.getFavorites().map { item -> item.toMedia(api.getBaseUrl()) }
        )
    }
}