package com.swackles.jellyfin.data.repository

import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto

enum class JellyfinResponses {
    SUCCESSFULL,
    UNAUTHORIZED_RESPONSE
}

interface JellyfinRepository {
    suspend fun login(hostname: String, username: String, password: String): JellyfinResponses

    suspend fun getItem(itemId: UUID): BaseItemDto

    suspend fun getContinueWatching(): List<BaseItemDto>

    suspend fun getNewlyAdded(): List<BaseItemDto>

    suspend fun getFavorites(): List<BaseItemDto>

    suspend fun getSimilar(itemId: UUID): List<BaseItemDto>

    suspend fun getEpisodes(seriesId: UUID): List<BaseItemDto>

    fun getBaseUrl(): String
}