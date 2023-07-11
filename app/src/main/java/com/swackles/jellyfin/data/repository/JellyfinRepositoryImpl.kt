package com.swackles.jellyfin.data.repository

import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.extensions.authenticateUserByName
import org.jellyfin.sdk.api.client.extensions.itemsApi
import org.jellyfin.sdk.api.client.extensions.libraryApi
import org.jellyfin.sdk.api.client.extensions.tvShowsApi
import org.jellyfin.sdk.api.client.extensions.userApi
import org.jellyfin.sdk.api.client.extensions.userLibraryApi
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind.MOVIE
import org.jellyfin.sdk.model.api.BaseItemKind.SERIES
import org.jellyfin.sdk.model.api.ItemFilter
import org.jellyfin.sdk.model.api.SortOrder.DESCENDING
import javax.inject.Inject

class JellyfinRepositoryImpl @Inject constructor(
    private val jellyfinClient: ApiClient
) : JellyfinRepository {
    override suspend fun login(username: String, password: String): Void? {
        val authenticationResult by this.jellyfinClient.userApi.authenticateUserByName(
            username,
            password,
        )

        this.jellyfinClient.userId = authenticationResult.user!!.id
        this.jellyfinClient.accessToken = authenticationResult.accessToken

        return null
    }

    override suspend fun getItem(itemId: UUID): BaseItemDto {
        return jellyfinClient.userLibraryApi.getItem(
            userId = getUserId(),
            itemId = itemId
        ).content
    }

    override suspend fun getContinueWatching(): List<BaseItemDto> {
        try {
            val nextTvShows = jellyfinClient.tvShowsApi.getNextUp(getUserId()).content.items ?: emptyList()
            val resumeItems = jellyfinClient.itemsApi.getResumeItems(getUserId()).content.items ?: emptyList()

            return nextTvShows + resumeItems
        } catch (e: Exception) {
            println(e.message)

            return emptyList()
        }
    }

    override suspend fun getNewlyAdded(): List<BaseItemDto> {
        return jellyfinClient.itemsApi.getItems(
            userId = getUserId(),
            includeItemTypes = listOf(MOVIE, SERIES),
            sortBy = listOf("DateCreated"),
            sortOrder = listOf(DESCENDING),
            recursive = true,
            limit = 10
        ).content.items ?: emptyList()
    }

    override suspend fun getFavorites(): List<BaseItemDto> {
        return jellyfinClient.itemsApi.getItems(
            userId = getUserId(),
            filters = listOf(ItemFilter.IS_FAVORITE),
            includeItemTypes = listOf(MOVIE, SERIES),
            recursive = true
        ).content.items ?: emptyList()
    }

    override suspend fun getSimilar(itemId: UUID): List<BaseItemDto> {
        return jellyfinClient.libraryApi.getSimilarItems(
            userId = getUserId(),
            itemId = itemId
        ).content.items ?: emptyList()
    }

    override suspend fun getEpisodes(seriesId: UUID): List<BaseItemDto> {
        return jellyfinClient.tvShowsApi.getEpisodes(
            userId = getUserId(),
            seriesId = seriesId
        ).content.items ?: emptyList()

    }

    override fun getBaseUrl(): String {
        return jellyfinClient.baseUrl!!
    }

    private fun getUserId(): UUID {
        return jellyfinClient.userId!!
    }
}