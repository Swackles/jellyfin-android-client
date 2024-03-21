package com.swackles.jellyfin.data.jellyfin.repository

import com.swackles.jellyfin.data.jellyfin.enums.MediaItemType
import com.swackles.jellyfin.data.jellyfin.models.DetailMediaBase
import com.swackles.jellyfin.data.jellyfin.models.GetMediaFilters
import com.swackles.jellyfin.data.jellyfin.models.Holder
import com.swackles.jellyfin.data.jellyfin.models.Media
import com.swackles.jellyfin.data.jellyfin.models.MediaFilters
import com.swackles.jellyfin.data.jellyfin.models.MediaSection
import org.jellyfin.sdk.model.UUID

interface MediaRepository {
    suspend fun getMedia(id: UUID): Holder<DetailMediaBase>
    suspend fun getMediaItems(filters: GetMediaFilters): List<Media>
    suspend fun getContinueWatching(): MediaSection
    suspend fun getNewlyAdded(): MediaSection
    suspend fun getRecommended(): MediaSection
    suspend fun getFilters(items: Collection<MediaItemType> = listOf(MediaItemType.SERIES, MediaItemType.MOVIE)): MediaFilters
}