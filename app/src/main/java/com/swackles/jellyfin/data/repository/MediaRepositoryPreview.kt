package com.swackles.jellyfin.data.repository

import com.swackles.jellyfin.common.Holder
import com.swackles.jellyfin.data.enums.MediaItemType
import com.swackles.jellyfin.data.models.DetailMediaBase
import com.swackles.jellyfin.data.models.MediaSection
import com.swackles.jellyfin.data.models.DetailMediaMoviePreview
import com.swackles.jellyfin.data.models.GetMediaFilters
import com.swackles.jellyfin.data.models.Media
import com.swackles.jellyfin.data.models.MediaFilters
import org.jellyfin.sdk.model.UUID

class MediaRepositoryPreview : MediaRepository {
    override suspend fun getMedia(id: UUID) = Holder.Success<DetailMediaBase>(
        DetailMediaMoviePreview()
    )

    override suspend fun getMediaItems(filters: GetMediaFilters) = emptyList<Media>()

    override suspend fun getContinueWatching() = MediaSection("", emptyList())

    override suspend fun getNewlyAdded() = MediaSection("", emptyList())

    override suspend fun getRecommended() = MediaSection("", emptyList())

    override suspend fun getFilters(items: Collection<MediaItemType>) =
        MediaFilters(
            genres = emptyList(),
            officialRatings = emptyList(),
            years = emptyList(),
        )
}