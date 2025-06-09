package com.swackles.jellyfin.data.jellyfin

import com.swackles.jellyfin.data.jellyfin.enums.MediaItemType
import com.swackles.jellyfin.data.jellyfin.mappers.mapToLibraryItems
import com.swackles.jellyfin.data.jellyfin.mappers.toMediaItem
import com.swackles.jellyfin.data.jellyfin.mappers.toPossibleFilters
import com.swackles.jellyfin.data.jellyfin.models.LibraryItem
import com.swackles.jellyfin.data.jellyfin.models.MediaFilters
import com.swackles.jellyfin.data.jellyfin.models.MediaItem
import com.swackles.jellyfin.data.jellyfin.models.PossibleFilters
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.extensions.filterApi
import org.jellyfin.sdk.api.client.extensions.itemsApi
import org.jellyfin.sdk.api.client.extensions.libraryApi
import org.jellyfin.sdk.api.client.extensions.tvShowsApi
import org.jellyfin.sdk.api.client.extensions.userLibraryApi
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.ItemFilter
import org.jellyfin.sdk.model.api.ItemSortBy
import org.jellyfin.sdk.model.api.SortOrder
import org.jellyfin.sdk.model.api.request.GetItemsRequest
import java.util.UUID

class LibraryServiceImpl(
    private val jellyfinClient: ApiClient
): LibraryService {
    override suspend fun getItem(id: UUID): MediaItem  {
        val item = jellyfinClient
            .userLibraryApi
            .getItem(itemId = id)
            .toMediaItem(jellyfinClient, getSimilar(id))

        if (item !is MediaItem.Series) return item

        return item.copy(episodes = this.getEpisodes(id))
    }

    override suspend fun getItems(filters: MediaFilters): List<LibraryItem> =
        jellyfinClient.itemsApi.getItems(
            GetItemsRequest(
                searchTerm = filters.query,
                genres = filters.genres,
                years = filters.years,
                officialRatings = filters.officialRatings,
                includeItemTypes = filters.mediaTypes,
                recursive = true
        )
        ).mapToLibraryItems(jellyfinClient)

    override suspend fun getContinueWatching(): List<LibraryItem> =
        jellyfinClient.itemsApi.getResumeItems().mapToLibraryItems(jellyfinClient) +
                jellyfinClient.tvShowsApi.getNextUp().mapToLibraryItems(jellyfinClient)

    override suspend fun getNewlyAdded(): List<LibraryItem> =
        jellyfinClient.itemsApi.getItems(
            GetItemsRequest(
                includeItemTypes = listOf(BaseItemKind.MOVIE, BaseItemKind.SERIES),
                sortBy = listOf(ItemSortBy.DATE_CREATED),
                sortOrder = listOf(SortOrder.DESCENDING),
                recursive = true,
                limit = LIMIT
            )
        ).mapToLibraryItems(jellyfinClient)

    override suspend fun getRecommended(): List<LibraryItem> =
        jellyfinClient.itemsApi.getItems(
            filters = listOf(ItemFilter.IS_FAVORITE),
            includeItemTypes = listOf(BaseItemKind.MOVIE, BaseItemKind.SERIES),
            recursive = true
        ).mapToLibraryItems(jellyfinClient)

    override suspend fun getFavorites(): List<LibraryItem> =
        jellyfinClient.itemsApi.getItems(
            filters = listOf(ItemFilter.IS_FAVORITE),
            includeItemTypes = listOf(BaseItemKind.MOVIE, BaseItemKind.SERIES),
            recursive = true
        ).mapToLibraryItems(jellyfinClient)

    override suspend fun getSimilar(id: UUID): List<LibraryItem> =
        jellyfinClient.libraryApi.getSimilarItems(
            itemId = id,
            limit = RECOMMENDED_LIMIT
        ).mapToLibraryItems(jellyfinClient)

    override suspend fun getEpisodes(id: UUID): List<LibraryItem.Episode> =
        jellyfinClient.tvShowsApi.getEpisodes(
            seriesId = id
        ).mapToLibraryItems(jellyfinClient).filter { it is LibraryItem.Episode } as List<LibraryItem.Episode>

    override suspend fun getFilters(): PossibleFilters =
        jellyfinClient.filterApi.getQueryFiltersLegacy(
            includeItemTypes = listOf(MediaItemType.MOVIE, MediaItemType.SERIES)
        ).toPossibleFilters()

    companion object {
        private const val LIMIT = 10;
        private const val RECOMMENDED_LIMIT = 10;
    }
}