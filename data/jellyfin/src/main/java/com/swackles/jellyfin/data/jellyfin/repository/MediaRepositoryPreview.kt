package com.swackles.jellyfin.data.jellyfin.repository

import com.swackles.jellyfin.data.jellyfin.enums.MediaItemType
import com.swackles.jellyfin.data.jellyfin.models.Holder
import com.swackles.jellyfin.data.jellyfin.models.DetailMediaBase
import com.swackles.jellyfin.data.jellyfin.models.MediaSection
import com.swackles.jellyfin.data.jellyfin.models.DetailMediaMoviePreview
import com.swackles.jellyfin.data.jellyfin.models.GetMediaFilters
import com.swackles.jellyfin.data.jellyfin.models.Media
import com.swackles.jellyfin.data.jellyfin.models.MediaFilters
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