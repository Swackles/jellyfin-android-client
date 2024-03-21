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
            isFavorite = false,
            playCount = 0,
            playbackPositionTicks = 0,
            played = false
        )
    ),
    "test-url"
) {
    override fun getImageUrl(size: Size) = drawable.toString()
}