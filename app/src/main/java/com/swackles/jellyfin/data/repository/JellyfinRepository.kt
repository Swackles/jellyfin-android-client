package com.swackles.jellyfin.data.repository

import com.swackles.jellyfin.data.enums.JellyfinResponses
import com.swackles.jellyfin.data.models.GetMediaFilters
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.PlaybackInfoResponse
import org.jellyfin.sdk.model.api.QueryFiltersLegacy

interface JellyfinRepository {
    suspend fun login(hostname: String, username: String, password: String): JellyfinResponses

    suspend fun getItem(itemId: UUID): BaseItemDto

    suspend fun getItems(filters: GetMediaFilters): List<BaseItemDto>

    suspend fun getContinueWatching(): List<BaseItemDto>

    suspend fun getNewlyAdded(): List<BaseItemDto>

    suspend fun getFavorites(): List<BaseItemDto>

    suspend fun getSimilar(itemId: UUID): List<BaseItemDto>

    suspend fun getEpisodes(seriesId: UUID): List<BaseItemDto>

    suspend fun getMetadata(itemId: UUID): PlaybackInfoResponse

    suspend fun getFilters(items: Collection<BaseItemKind>): QueryFiltersLegacy

    fun getHeaders(): HashMap<String, String>

    fun getBaseUrl(): String
}