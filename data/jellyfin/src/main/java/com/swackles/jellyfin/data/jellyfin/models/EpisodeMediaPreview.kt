package com.swackles.jellyfin.data.jellyfin.models

import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.UserItemDataDto
import java.util.UUID

class EpisodeMediaPreview(
    playedPercentage: Float,
    episode: Int,
    season: Int,
    private val drawable: Int,
    durationInMinutes: Long = 24
): EpisodeMedia(
    BaseItemDto(
        id = UUID.randomUUID(),
        type = BaseItemKind.EPISODE,
        indexNumber = episode,
        parentIndexNumber = season,
        name = "Lorem Ipsum",
        overview = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a risus enim. Nullam nulla.",
        runTimeTicks = durationInMinutes * 600000000,
        userData = UserItemDataDto(
            playbackPositionTicks = 0,
            playCount = 0,
            isFavorite = false,
            played = playedPercentage == 100f,
            key = "test-key",
            itemId = UUID.randomUUID(),
            playedPercentage = (playedPercentage * 100).toDouble()
        )
    ),
    "test-url"
) {
    override fun getImageUrl(size: Size) = drawable.toString()
}