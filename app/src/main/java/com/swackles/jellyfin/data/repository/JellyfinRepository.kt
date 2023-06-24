package com.swackles.jellyfin.data.repository

import org.jellyfin.sdk.model.api.BaseItemDto

interface JellyfinRepository {
    suspend fun login(username: String, password: String): Void?

    suspend fun getContinueWatching(): List<BaseItemDto>

    suspend fun getNewlyAdded(): List<BaseItemDto>

    suspend fun getFavorites(): List<BaseItemDto>

    fun getBaseUrl(): String
}