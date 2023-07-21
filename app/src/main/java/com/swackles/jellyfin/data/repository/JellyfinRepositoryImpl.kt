package com.swackles.jellyfin.data.repository

import android.content.Context
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.exception.InvalidStatusException
import org.jellyfin.sdk.api.client.extensions.authenticateUserByName
import org.jellyfin.sdk.api.client.extensions.itemsApi
import org.jellyfin.sdk.api.client.extensions.libraryApi
import org.jellyfin.sdk.api.client.extensions.tvShowsApi
import org.jellyfin.sdk.api.client.extensions.userApi
import org.jellyfin.sdk.api.client.extensions.userLibraryApi
import org.jellyfin.sdk.createJellyfin
import org.jellyfin.sdk.model.ClientInfo
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind.MOVIE
import org.jellyfin.sdk.model.api.BaseItemKind.SERIES
import org.jellyfin.sdk.model.api.ItemFilter
import org.jellyfin.sdk.model.api.SortOrder.DESCENDING
import javax.inject.Inject

class JellyfinRepositoryImpl @Inject constructor(
    private val jellyfinClient: ApiClient,
    private val context: Context
) : JellyfinRepository {
    override suspend fun login(hostname: String, username: String, password: String): JellyfinResponses {
        val client = createJellyfin(this.context).createApi(hostname)
        try {
            val authenticationResult by client.userApi.authenticateUserByName(username, password)

            client.userId = authenticationResult.user!!.id
            client.accessToken = authenticationResult.accessToken

        } catch (err: InvalidStatusException) {
            return when (err.status) {
                401 -> JellyfinResponses.UNAUTHORIZED_RESPONSE
                else -> {
                    throw err
                }
            }
        }

        INSTANCE = client
        return JellyfinResponses.SUCCESSFULL
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

    companion object {
        @Volatile
        private var INSTANCE: ApiClient? = null

        fun getInstance(context: Context): ApiClient {
            return INSTANCE ?: createJellyfin(context).createApi("").also { INSTANCE = it }
        }

        private fun createJellyfin(appContext: Context): Jellyfin {
            return createJellyfin {
                context = appContext
                clientInfo = ClientInfo(
                    name = "Jellyfin WIP app",
                    version = "0.1-ALPHA"
                )
            }
        }
    }
}