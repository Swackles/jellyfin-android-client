package com.swackles.jellyfin.domain.models

import androidx.compose.ui.text.style.TextAlign
import org.jellyfin.sdk.model.api.BaseItemDto

open class DetailMediaSeries(
    private val baseItem: BaseItemDto,
    similar: List<Media>,
    private val episodes: List<EpisodeMedia>,
    baseUrl: String
) : DetailMediaBase(
    baseItem,
    similar,
    episodes,
    baseUrl
) {
    override fun getPlayShortcutInfo(): PlayShortcutInfo {
        val episode = episodes.firstOrNull { it.isInProgress() } ?: episodes.first()

        return PlayShortcutInfo(
            progress = episode.progress(),
            labels = listOf(
                DetailMediaBarLabels("S${episode.season} E${episode.episode} \"${episode.title}\"", TextAlign.Left),
                DetailMediaBarLabels(episode.getDurationString(), TextAlign.Right),
            ),
            mediaId = episode.id,
            startPosition = episode.playbackPositionTicks,
            isInProgress = true
        )
    }

    override fun getSeasons() = episodes.mapNotNull { it.season }.distinct()

    override fun getInfo() = listOfNotNull(
        baseItem.premiereDate?.year.toString(),
        genres.first()
    )
}