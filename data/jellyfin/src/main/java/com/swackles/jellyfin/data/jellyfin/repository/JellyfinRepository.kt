package com.swackles.jellyfin.data.jellyfin.repository

import com.swackles.jellyfin.data.jellyfin.models.GetMediaFilters
import com.swackles.jellyfin.data.jellyfin.models.JellyfinAuthResponse
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.PlaybackInfoResponse
import org.jellyfin.sdk.model.api.QueryFiltersLegacy

interface JellyfinRepository {
    /**
     * Checks the Jellyfin server for the user's authentication status and sets repository's user information
     *
     * @param hostname The hostname of the Jellyfin server
     * @param token The token to use for authentication
     * @param deviceId Jellyfin only allows one device to be authenticated at a time, so we generate it at inital login and reuse it
     * @return A [JellyfinAuthResponse] with the response and user information
     */
    suspend fun login(hostname: String, userId: UUID, token: String, deviceId: String): JellyfinAuthResponse

    /**
     * Logs into the Jellyfin server with the provided credentials
     *
     * @param hostname The hostname of the Jellyfin server
     * @param username The username to log in with
     * @param password The password to log in with
     * @return A [JellyfinAuthResponse] with the response and user information
     */
    suspend fun login(hostname: String, username: String, password: String): JellyfinAuthResponse

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