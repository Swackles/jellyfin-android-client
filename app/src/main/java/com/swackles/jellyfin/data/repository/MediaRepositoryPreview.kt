package com.swackles.jellyfin.data.repository

import com.swackles.jellyfin.common.Holder
import com.swackles.jellyfin.data.models.DetailMediaBase
import com.swackles.jellyfin.data.models.MediaSection
import com.swackles.jellyfin.data.models.DetailMediaMoviePreview
import org.jellyfin.sdk.model.UUID

class MediaRepositoryPreview : MediaRepository {
    override suspend fun getMedia(id: UUID) = Holder.Success<DetailMediaBase>(
        DetailMediaMoviePreview()
    )

    override suspend fun getContinueWatching() = MediaSection("", emptyList())

    override suspend fun getNewlyAdded() = MediaSection("", emptyList())

    override suspend fun getRecommended() = MediaSection("", emptyList())
}