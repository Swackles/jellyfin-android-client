package com.swackles.jellyfin.data.jellyfin.models

import com.swackles.jellyfin.data.jellyfin.R
import org.jellyfin.sdk.model.DateTime
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.UserItemDataDto
import java.util.UUID

class EpisodeMediaMissingPreview(
    episode: Int,
    season: Int,
    premiereDate: DateTime,
    private val drawable: Int = R.drawable.series_backdrop_image,
): EpisodeMedia(
    BaseItemDto(
        id = UUID.randomUUID(),
        type = BaseItemKind.EPISODE,
        indexNumber = episode,
        parentIndexNumber = season,
        name = "Episode $episode",
        premiereDate = premiereDate,
        userData = UserItemDataDto(
            playbackPositionTicks = 0,
            playCount = 0,
            isFavorite = false,
            played = false,
            key = "test-key",
            itemId = UUID.randomUUID(),
        )
    ),
    "test-url"
) {
    override fun getImageUrl(size: Size) = drawable.toString()
}