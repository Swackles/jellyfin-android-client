package com.swackles.jellyfin.presentation.common.preview

import com.swackles.jellyfin.data.jellyfin.models.LibraryItem
import java.time.LocalDateTime
import java.util.UUID

fun LibraryItem.Companion.preview(playedPercentage: Double = 0.0): LibraryItem {
    return LibraryItem.Movie(
        id = UUID.randomUUID(),
        playedPercentage = playedPercentage,
        baseUrl = ""
    )
}

fun LibraryItem.Episode.Companion.preview(
    playedPercentage: Float,
    episode: Int,
    season: Int,
    durationInMinutes: Long = 24
): LibraryItem.Episode = LibraryItem.Episode(
    id = UUID.randomUUID(),
    seriesId = UUID.randomUUID(),
    episode = episode,
    season = season,
    premiereDate = LocalDateTime.now(),
    title = "Lorem Ipsum",
    overview = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a risus enim. Nullam nulla.",
    isMissing = false,
    playbackPositionTicks = durationInMinutes * durationInMinutes / 100,
    hasAired = true,
    baseUrl = "test-url",
    playedPercentage = playedPercentage,
    isCompleted = playedPercentage == 1f,
    runtimeTicks = (playedPercentage * durationInMinutes).toLong()
)

fun LibraryItem.Episode.Companion.missingPreview(
    episode: Int,
    season: Int,
    premiereDate: LocalDateTime,
): LibraryItem.Episode = LibraryItem.Episode(
    id = UUID.randomUUID(),
    seriesId = UUID.randomUUID(),
    episode = episode,
    season = season,
    premiereDate = premiereDate,
    title = "Lorem Ipsum",
    overview = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a risus enim. Nullam nulla.",
    isMissing = true,
    playbackPositionTicks = 0L,
    hasAired = true,
    baseUrl = "test-url",
    playedPercentage = 0f,
    isCompleted = false,
    runtimeTicks = 0L
)