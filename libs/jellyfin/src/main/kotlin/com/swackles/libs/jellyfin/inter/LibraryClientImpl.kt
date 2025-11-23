package com.swackles.libs.jellyfin.inter

import com.swackles.libs.jellyfin.JellyfinClientErrors.BadDataFormatError
import com.swackles.libs.jellyfin.JellyfinFilters
import com.swackles.libs.jellyfin.LibraryClient
import com.swackles.libs.jellyfin.LibraryFilters
import com.swackles.libs.jellyfin.LibraryItem
import com.swackles.libs.jellyfin.Pagination
import com.swackles.libs.jellyfin.PaginationResponse
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.Response
import org.jellyfin.sdk.api.client.extensions.filterApi
import org.jellyfin.sdk.api.client.extensions.itemsApi
import org.jellyfin.sdk.api.client.extensions.libraryApi
import org.jellyfin.sdk.api.client.extensions.tvShowsApi
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemDtoQueryResult
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.ItemFields
import org.jellyfin.sdk.model.api.ItemFilter
import org.jellyfin.sdk.model.api.ItemSortBy
import org.jellyfin.sdk.model.api.QueryFiltersLegacy
import org.jellyfin.sdk.model.api.SortOrder
import org.jellyfin.sdk.model.api.request.GetItemsRequest
import org.jellyfin.sdk.model.api.request.GetNextUpRequest
import org.jellyfin.sdk.model.api.request.GetResumeItemsRequest
import java.time.LocalDateTime
import java.util.UUID

internal class LibraryClientImpl(
    private val jellyfinClient: ApiClient
): LibraryClient {
    override suspend fun getFilters(): JellyfinFilters =
        jellyfinClient.filterApi.getQueryFiltersLegacy(
            includeItemTypes = listOf(BaseItemKind.MOVIE, BaseItemKind.SERIES)
        ).toPossibleFilters()

    override suspend fun search(filters: LibraryFilters, pagination: Pagination): PaginationResponse {
        val res = jellyfinClient.itemsApi.getItems(
            request = GetItemsRequest(
                searchTerm = filters.query,
                genres = filters.genres,
                years = filters.years,
                officialRatings = filters.officialRatings,
                includeItemTypes = filters.mediaTypes,
                recursive = true,
                startIndex = pagination.limit * pagination.page,
                limit = pagination.limit
            )
        )

        return PaginationResponse(
            items = res.mapToLibraryItems(jellyfinClient),
            page = pagination.page,
            limit = pagination.limit,
            totalRecordCount = res.content.totalRecordCount
        )
    }

    override suspend fun getContinueWatching(): List<LibraryItem> =
        jellyfinClient.itemsApi.getResumeItems(GetResumeItemsRequest(limit = LIMIT)).content.items
            .plus(jellyfinClient.tvShowsApi.getNextUp(GetNextUpRequest(limit = LIMIT)).content.items)
            .sortedByDescending { it.userData?.lastPlayedDate ?: LocalDateTime.MIN }
            .take(LIMIT)
            .map { it.toLibraryItem(jellyfinClient.baseUrl!!) }

    override suspend fun getNewlyAdded(): List<LibraryItem> =
        jellyfinClient.itemsApi.getItems(
            GetItemsRequest(
                includeItemTypes = listOf(BaseItemKind.MOVIE, BaseItemKind.SERIES),
                sortBy = listOf(ItemSortBy.DATE_LAST_CONTENT_ADDED),
                sortOrder = listOf(SortOrder.DESCENDING),
                recursive = true,
                limit = LIMIT
            )
        ).mapToLibraryItems(jellyfinClient)

    override suspend fun getFavorites(): List<LibraryItem> =
        jellyfinClient.itemsApi.getItems(
            GetItemsRequest(
                includeItemTypes = listOf(BaseItemKind.MOVIE, BaseItemKind.SERIES),
                sortBy = listOf(ItemSortBy.DATE_PLAYED),
                sortOrder = listOf(SortOrder.DESCENDING),
                fields = listOf(ItemFields.GENRES),
                recursive = true
            )
        ).mapToLibraryItems(jellyfinClient)

    override suspend fun getRecommended(): List<LibraryItem> =
        jellyfinClient.itemsApi.getItems(
            filters = listOf(ItemFilter.IS_FAVORITE),
            includeItemTypes = listOf(BaseItemKind.MOVIE, BaseItemKind.SERIES),
            recursive = true,
            limit = LIMIT
        ).mapToLibraryItems(jellyfinClient)


    override suspend fun getSimilar(id: UUID): List<LibraryItem> =
        jellyfinClient.libraryApi.getSimilarItems(
            itemId = id,
            limit = RECOMMENDED_LIMIT
        ).mapToLibraryItems(jellyfinClient)

    private fun Response<QueryFiltersLegacy>.toPossibleFilters(): JellyfinFilters =
        JellyfinFilters(
            genres = content.genres.orEmpty(),
            tags = content.tags.orEmpty(),
            ratings = content.officialRatings.orEmpty(),
            years = content.years.orEmpty()
        )

    private fun BaseItemDto.toLibraryItem(baseUrl: String): LibraryItem =
        when (type) {
            BaseItemKind.MOVIE -> LibraryItem.Movie(
                id = id,
                title = name ?: "",
                playedPercentage = userData?.playedPercentage.convertToPlayedPercentage(),
                baseUrl = baseUrl,
                playbackPositionTicks = userData?.playbackPositionTicks ?: 0L,
                genres = genres ?: emptyList()
            )
            BaseItemKind.SERIES -> LibraryItem.Series(
                id = id,
                playedPercentage = userData?.playedPercentage.convertToPlayedPercentage(),
                baseUrl = baseUrl,
                playbackPositionTicks = userData?.playbackPositionTicks ?: 0L,
                genres = genres ?: emptyList()
            )
            BaseItemKind.EPISODE -> LibraryItem.Episode(
                id = id,
                title = name ?: "",
                seriesTitle = seriesName ?: "",
                episode = indexNumber ?: -1,
                season = parentIndexNumber ?: -1,
                playedPercentage = userData?.playedPercentage.convertToPlayedPercentage(),
                playbackPositionTicks = userData?.playbackPositionTicks ?: 0L,
                baseUrl = baseUrl
            )
            else -> throw BadDataFormatError("Unknown media type \"$type\"")
        }

    private fun Double?.convertToPlayedPercentage(): Float =
        (this ?: 0).toFloat() / 100

    fun Response<BaseItemDtoQueryResult>.mapToLibraryItems(jellyfinClient: ApiClient): List<LibraryItem> =
        this.content.items.map { it.toLibraryItem(jellyfinClient.baseUrl!!) }

    companion object {
        private const val LIMIT = 10
        private const val RECOMMENDED_LIMIT = 10
    }
}