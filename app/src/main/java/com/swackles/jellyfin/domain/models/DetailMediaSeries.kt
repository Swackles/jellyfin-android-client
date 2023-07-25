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
    override val isInProgress = true

    override fun getSeasons() = episodes.mapNotNull { it.season }.distinct()

    override fun getDurationString() = getInProgressEpisode().getDurationString()

    override fun getInfo() = listOfNotNull(
        baseItem.premiereDate?.year.toString(),
        genres.first()
    )

    override fun getProgressBarLabels(): List<DetailMediaBarLabels> {
        val episode = getInProgressEpisode()

        return listOf(
            DetailMediaBarLabels("S${episode.season} E${episode.episode} \"${episode.title}\"", TextAlign.Left),
            DetailMediaBarLabels(getDurationString(), TextAlign.Right),
        )
    }

    private fun getInProgressEpisode() = episodes.firstOrNull { it.isInProgress() } ?: episodes.first()
}