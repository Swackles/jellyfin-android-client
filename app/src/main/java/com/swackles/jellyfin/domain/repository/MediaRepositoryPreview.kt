package com.swackles.jellyfin.domain.repository

import com.swackles.jellyfin.common.Holder
import com.swackles.jellyfin.domain.models.DetailMediaBase
import com.swackles.jellyfin.domain.models.MediaSection
import com.swackles.jellyfin.domain.models.DetailMediaMoviePreview
import org.jellyfin.sdk.model.UUID

class MediaRepositoryPreview : MediaRepository {
    override suspend fun getMedia(id: UUID) = Holder.Success<DetailMediaBase>(DetailMediaMoviePreview())

    override suspend fun getContinueWatching() = MediaSection("", emptyList())

    override suspend fun getNewlyAdded() = MediaSection("", emptyList())

    override suspend fun getRecommended() = MediaSection("", emptyList())
}