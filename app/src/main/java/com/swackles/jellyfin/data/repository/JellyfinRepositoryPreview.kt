package com.swackles.jellyfin.data.repository

import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind

class JellyfinRepositoryPreview: JellyfinRepository {
    override suspend fun login(hostname: String, username: String, password: String) = JellyfinResponses.SUCCESSFULL

    override suspend fun getItem(itemId: UUID) = BaseItemDto(
        id = UUID.randomUUID(),
        type = BaseItemKind.AUDIO)

    override suspend fun getContinueWatching() = emptyList<BaseItemDto>()

    override suspend fun getNewlyAdded() = emptyList<BaseItemDto>()

    override suspend fun getFavorites() = emptyList<BaseItemDto>()

    override suspend fun getSimilar(itemId: UUID) = emptyList<BaseItemDto>()

    override suspend fun getEpisodes(seriesId: UUID) = emptyList<BaseItemDto>()

    override fun getBaseUrl() = ""
}