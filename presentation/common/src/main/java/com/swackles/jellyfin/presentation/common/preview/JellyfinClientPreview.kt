package com.swackles.jellyfin.presentation.common.preview

import com.swackles.jellyfin.data.jellyfin.JellyfinClient
import com.swackles.jellyfin.data.jellyfin.LibraryService
import com.swackles.jellyfin.data.jellyfin.MediaService
import com.swackles.jellyfin.data.jellyfin.models.JellyfinUser
import com.swackles.jellyfin.data.jellyfin.models.LibraryItem
import com.swackles.jellyfin.data.jellyfin.models.MediaFilters
import com.swackles.jellyfin.data.jellyfin.models.MediaItem
import com.swackles.jellyfin.data.jellyfin.models.PossibleFilters
import com.swackles.jellyfin.data.jellyfin.models.VideoMetadata
import java.util.UUID

class JellyfinClientPreview: JellyfinClient {
    override val libraryService: LibraryService
        get() = object : LibraryService {
            override suspend fun getItem(id: UUID): MediaItem {
                TODO("Not yet implemented")
            }

            override suspend fun getItems(filters: MediaFilters): List<LibraryItem> {
                TODO("Not yet implemented")
            }

            override suspend fun getContinueWatching(): List<LibraryItem> {
                TODO("Not yet implemented")
            }

            override suspend fun getNewlyAdded(): List<LibraryItem> {
                TODO("Not yet implemented")
            }

            override suspend fun getRecommended(): List<LibraryItem> {
                TODO("Not yet implemented")
            }

            override suspend fun getFavorites(): List<LibraryItem> {
                TODO("Not yet implemented")
            }

            override suspend fun getSimilar(id: UUID): List<LibraryItem> {
                TODO("Not yet implemented")
            }

            override suspend fun getEpisodes(id: UUID): List<LibraryItem.Episode> {
                TODO("Not yet implemented")
            }

            override suspend fun getFilters(): PossibleFilters {
                TODO("Not yet implemented")
            }
        }
    override val mediaService: MediaService
        get() = object : MediaService {
            override suspend fun getMetadataUsingId(id: UUID): VideoMetadata {
                TODO("Not yet implemented")
            }
        }

    override suspend fun logout(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getJellyfinUser(): JellyfinUser {
        TODO("Not yet implemented")
    }

    override fun getHeaders(): HashMap<String, String> {
        TODO("Not yet implemented")
    }

}