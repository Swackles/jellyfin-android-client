package com.swackles.jellyfin.data.repository

import com.swackles.jellyfin.data.enums.JellyfinResponses
import com.swackles.jellyfin.data.models.GetMediaFilters
import com.swackles.jellyfin.data.models.JellyfinAuthResponse
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.PlaybackInfoResponse
import org.jellyfin.sdk.model.api.QueryFiltersLegacy

class JellyfinRepositoryPreview: JellyfinRepository {
    override suspend fun login(hostname: String, username: String, password: String) = JellyfinAuthResponse(
        JellyfinResponses.SUCCESSFUL,
        null
    )

    override suspend fun getItem(itemId: UUID) = BaseItemDto(
        id = UUID.randomUUID(),
        type = BaseItemKind.AUDIO)

    override suspend fun getItems(filters: GetMediaFilters) = emptyList<BaseItemDto>()

    override suspend fun getContinueWatching() = emptyList<BaseItemDto>()

    override suspend fun getNewlyAdded() = emptyList<BaseItemDto>()

    override suspend fun getFavorites() = emptyList<BaseItemDto>()

    override suspend fun getSimilar(itemId: UUID) = emptyList<BaseItemDto>()

    override suspend fun getEpisodes(seriesId: UUID) = emptyList<BaseItemDto>()

    override suspend fun getMetadata(itemId: UUID) = PlaybackInfoResponse(emptyList())

    override suspend fun getFilters(items: Collection<BaseItemKind>): QueryFiltersLegacy =
        QueryFiltersLegacy(
            genres = emptyList(),
            officialRatings = emptyList(),
            years = emptyList()
        )

    override fun getHeaders() = HashMap<String, String>()

    override fun getBaseUrl() = ""
}