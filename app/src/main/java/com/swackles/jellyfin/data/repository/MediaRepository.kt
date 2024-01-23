package com.swackles.jellyfin.data.repository

import com.swackles.jellyfin.common.Holder
import com.swackles.jellyfin.data.enums.MediaItemType
import com.swackles.jellyfin.data.models.DetailMediaBase
import com.swackles.jellyfin.data.models.GetMediaFilters
import com.swackles.jellyfin.data.models.Media
import com.swackles.jellyfin.data.models.MediaFilters
import com.swackles.jellyfin.data.models.MediaSection
import org.jellyfin.sdk.model.UUID

interface MediaRepository {
    suspend fun getMedia(id: UUID): Holder<DetailMediaBase>
    suspend fun getMediaItems(filters: GetMediaFilters): List<Media>
    suspend fun getContinueWatching(): MediaSection
    suspend fun getNewlyAdded(): MediaSection
    suspend fun getRecommended(): MediaSection
    suspend fun getFilters(items: Collection<MediaItemType> = listOf(MediaItemType.SERIES, MediaItemType.MOVIE)): MediaFilters
}