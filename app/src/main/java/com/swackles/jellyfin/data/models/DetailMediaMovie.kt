package com.swackles.jellyfin.data.models

import androidx.compose.ui.text.style.TextAlign
import com.swackles.jellyfin.data.extensions.durationString
import com.swackles.jellyfin.data.extensions.isInProgress
import com.swackles.jellyfin.data.extensions.playedPercentage
import org.jellyfin.sdk.model.api.BaseItemDto

open class DetailMediaMovie(
    private val baseItem: BaseItemDto,
    similar: List<Media>,
    baseUrl: String
) : DetailMediaBase(
    baseItem,
    similar,
    emptyList(),
    baseUrl
) {
    override fun getSeasons() = emptyList<Int>()
    override fun getEpisodes() = emptyMap<Int, List<EpisodeMedia>>()

    override fun getInfo() = listOfNotNull(
        baseItem.premiereDate?.year.toString(),
        baseItem.durationString(),
        genres.first()
    )

    override fun getPlayShortcutInfo(): PlayShortcutInfo {
        return PlayShortcutInfo(
            progress = baseItem.playedPercentage(),
            labels = listOf(DetailMediaBarLabels(baseItem.durationString(), TextAlign.Right)),
            mediaId = this.id,
            startPosition = baseItem.userData?.playbackPositionTicks ?: 0,
            isInProgress =  baseItem.isInProgress()
        )
    }
}