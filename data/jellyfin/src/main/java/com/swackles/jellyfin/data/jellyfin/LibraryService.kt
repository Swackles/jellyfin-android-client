package com.swackles.jellyfin.data.jellyfin

import com.swackles.jellyfin.data.jellyfin.models.LibraryItem
import com.swackles.jellyfin.data.jellyfin.models.MediaFilters
import com.swackles.jellyfin.data.jellyfin.models.MediaItem
import com.swackles.jellyfin.data.jellyfin.models.PossibleFilters
import java.util.UUID

interface LibraryService {
    suspend fun getItems(filters: MediaFilters): List<LibraryItem>

    suspend fun getContinueWatching(): List<LibraryItem>

    suspend fun getNewlyAdded(): List<LibraryItem>

    suspend fun getRecommended(): List<LibraryItem>

    suspend fun getFavorites(): List<LibraryItem>

    suspend fun getSimilar(id: UUID): List<LibraryItem>

    suspend fun getEpisodes(id: UUID): List<LibraryItem>

    /**
     * Retrieves the filters available for the media type.
     *
     * @return A [PossibleFilters] filters that can be used as [getItems] request input.
     */
    suspend fun getFilters(): PossibleFilters
}