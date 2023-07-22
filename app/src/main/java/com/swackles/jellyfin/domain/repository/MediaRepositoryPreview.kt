package com.swackles.jellyfin.domain.repository

import com.swackles.jellyfin.domain.models.DetailMedia
import com.swackles.jellyfin.domain.models.MediaSection
import org.jellyfin.sdk.model.DateTime
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemKind.SERIES

class MediaRepositoryPreview : MediaRepository {
    override suspend fun getMedia(id: UUID) = DetailMedia(
        UUID.randomUUID(),
        "",
        emptyList(),
        emptyList(),
        "",
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList(),
        SERIES,
        DateTime.now(),
        1,
        ""
    )

    override suspend fun getContinueWatching() = MediaSection("", emptyList())

    override suspend fun getNewlyAdded() = MediaSection("", emptyList())

    override suspend fun getRecommended() = MediaSection("", emptyList())
}