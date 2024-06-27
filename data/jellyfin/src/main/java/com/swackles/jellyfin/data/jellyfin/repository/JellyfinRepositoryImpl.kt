package com.swackles.jellyfin.data.jellyfin.repository

import android.content.Context
import com.swackles.jellyfin.data.jellyfin.enums.JellyfinResponses
import com.swackles.jellyfin.data.jellyfin.extensions.toJellyfinUser
import com.swackles.jellyfin.data.jellyfin.models.GetMediaFilters
import com.swackles.jellyfin.data.jellyfin.models.JellyfinAuthResponse
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.exception.InvalidStatusException
import org.jellyfin.sdk.api.client.extensions.authenticateUserByName
import org.jellyfin.sdk.api.client.extensions.filterApi
import org.jellyfin.sdk.api.client.extensions.itemsApi
import org.jellyfin.sdk.api.client.extensions.libraryApi
import org.jellyfin.sdk.api.client.extensions.mediaInfoApi
import org.jellyfin.sdk.api.client.extensions.sessionApi
import org.jellyfin.sdk.api.client.extensions.tvShowsApi
import org.jellyfin.sdk.api.client.extensions.userApi
import org.jellyfin.sdk.api.client.extensions.userLibraryApi
import org.jellyfin.sdk.createJellyfin
import org.jellyfin.sdk.model.ClientInfo
import org.jellyfin.sdk.model.DeviceInfo
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.BaseItemKind.MOVIE
import org.jellyfin.sdk.model.api.BaseItemKind.SERIES
import org.jellyfin.sdk.model.api.DeviceProfile
import org.jellyfin.sdk.model.api.ItemFilter
import org.jellyfin.sdk.model.api.LocationType
import org.jellyfin.sdk.model.api.PlaybackInfoDto
import org.jellyfin.sdk.model.api.PlaybackInfoResponse
import org.jellyfin.sdk.model.api.QueryFiltersLegacy
import org.jellyfin.sdk.model.api.SortOrder.DESCENDING
import org.jellyfin.sdk.model.api.SubtitleDeliveryMethod
import org.jellyfin.sdk.model.api.SubtitleProfile
import javax.inject.Inject


internal class JellyfinRepositoryImpl @Inject constructor(
    private var jellyfinClient: ApiClient,
    private val context: Context
) : JellyfinRepository {
    override suspend fun login(hostname: String, userId: UUID, token: String, deviceId: String): JellyfinAuthResponse {
        val client = createJellyfin(this.context, deviceId).createApi(
            baseUrl = hostname,
            accessToken = token,
            userId = userId
        )

        return try {
            val res by client.userApi.getCurrentUser()

            // TODO: Better way of updating client when user changes
            INSTANCE = client
            jellyfinClient = client
            JellyfinAuthResponse(
                response = JellyfinResponses.SUCCESSFUL,
                user = res.toJellyfinUser(client.baseUrl!!, token, deviceId)
            )
        } catch (ex: InvalidStatusException) {
            when (ex.status) {
                401 -> JellyfinAuthResponse(response = JellyfinResponses.UNAUTHORIZED_RESPONSE, user = null)
                else -> throw ex
            }
        } catch (ex: RuntimeException) {
            throw ex
        }
    }
    override suspend fun login(hostname: String, username: String, password: String): JellyfinAuthResponse {
        val deviceId = UUID.randomUUID().toString()

        val client = createJellyfin(this.context, deviceId).createApi(hostname)
        return try {
            val authenticationResult by client.userApi.authenticateUserByName(username, password)

            login(hostname, authenticationResult.user!!.id, authenticationResult.accessToken!!,deviceId)
        } catch (err: InvalidStatusException) {
            when (err.status) {
                401 -> JellyfinAuthResponse(response = JellyfinResponses.UNAUTHORIZED_RESPONSE, user = null)
                else -> {
                    throw err
                }
            }
        }
    }

    override suspend fun logout() {
        jellyfinClient.sessionApi.reportSessionEnded()
        INSTANCE = null
    }

    override suspend fun getItem(itemId: UUID): BaseItemDto {
        return jellyfinClient.userLibraryApi.getItem(
            userId = getUserId(),
            itemId = itemId
        ).content
    }

    override suspend fun getItems(filters: GetMediaFilters): List<BaseItemDto> {
        return jellyfinClient.itemsApi.getItems(
            userId = getUserId(),
            searchTerm = filters.query,
            genres = filters.genres,
            years = filters.years,
            officialRatings = filters.officialRatings,
            includeItemTypes = filters.mediaTypes.map { it.baseItem },
            recursive = true
        ).content.items ?: emptyList(
        )
    }

    override suspend fun getContinueWatching(): List<BaseItemDto> {
        try {
            val nextTvShows = jellyfinClient.tvShowsApi.getNextUp(getUserId()).content.items ?: emptyList()
            val resumeItems = jellyfinClient.itemsApi.getResumeItems(getUserId()).content.items ?: emptyList()

            return nextTvShows + resumeItems
        } catch (e: Exception) {
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
            itemId = itemId,
            limit = RECCOMENDED_COUNT
        ).content.items ?: emptyList()
    }

    override suspend fun getEpisodes(seriesId: UUID): List<BaseItemDto> {
        val episodes = jellyfinClient.tvShowsApi.getEpisodes(
            userId = getUserId(),
            seriesId = seriesId
        ).content.items ?: emptyList()

        return episodes.filter { filterDuplicateEpisodes(episodes, it) }
    }

    override suspend fun getMetadata(itemId: UUID): PlaybackInfoResponse {
        val body = PlaybackInfoDto(
            userId = getUserId(),
            deviceProfile = DeviceProfile(
                codecProfiles = emptyList(),
                containerProfiles = emptyList(),
                directPlayProfiles = emptyList(),
                responseProfiles = emptyList(),
                supportedMediaTypes = "",
                transcodingProfiles = emptyList(),
                xmlRootAttributes = emptyList(),
                subtitleProfiles = listOf(
                    SubtitleProfile(format = "vtt", method = SubtitleDeliveryMethod.EXTERNAL),
                    SubtitleProfile(format = "ass", method = SubtitleDeliveryMethod.EXTERNAL),
                    SubtitleProfile(format = "ssa", method = SubtitleDeliveryMethod.EXTERNAL)
                )
            )
        )

        return jellyfinClient.mediaInfoApi.getPostedPlaybackInfo(itemId, body).content
    }

    override fun getBaseUrl(): String {
        jellyfinClient.clientInfo.name
        return jellyfinClient.baseUrl!!
    }

    override fun getHeaders(): HashMap<String, String> {
        val values = listOf(
            "Token=\"${jellyfinClient.accessToken}\"",
            "Client=\"${jellyfinClient.clientInfo.name}\"",
            "Version=\"${jellyfinClient.clientInfo.version}\"",
            "DeviceId=\"${jellyfinClient.deviceInfo.id}\"",
            "Device=\"${jellyfinClient.deviceInfo.name}\""
        )

        return hashMapOf(Pair("Authorization", "MediaBrowser ${values.joinToString(", ")}"))
    }

    override suspend fun getFilters(items: Collection<BaseItemKind>): QueryFiltersLegacy =
        jellyfinClient.filterApi.getQueryFiltersLegacy(
            userId = getUserId(),
            includeItemTypes = items
        ).content


    private fun getUserId(): UUID {
        return jellyfinClient.userId!!
    }

    // Due to bug with TVDB not removing missing media items after they created, need to filter out those missing items
    private fun filterDuplicateEpisodes(episodes: List<BaseItemDto>, episode: BaseItemDto): Boolean {
        val fileSystemEpisode = episodes.find {
            it.locationType == LocationType.FILE_SYSTEM && it.indexNumber == episode.indexNumber && it.parentIndexNumber == episode.parentIndexNumber
        }

        return episode.locationType == LocationType.FILE_SYSTEM || fileSystemEpisode == null
    }

    companion object {
        private val RECCOMENDED_COUNT = 9

        @Volatile
        private var INSTANCE: ApiClient? = null

        fun getInstance(context: Context): ApiClient {
            return INSTANCE ?: createJellyfin(context, UUID.randomUUID().toString()).createApi("").also { INSTANCE = it }
        }

        private fun createJellyfin(appContext: Context, deviceId: String): Jellyfin {
            return createJellyfin {
                context = appContext
                deviceInfo = DeviceInfo(
                    id = deviceId,
                    name = android.os.Build.MODEL
                )
                clientInfo = ClientInfo(
                    name = "Jellyfin WIP app",
                    version = "0.1-ALPHA"
                )
            }
        }
    }
}