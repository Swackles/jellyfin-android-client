package com.swackles.jellyfin.data.jellyfin.models

import java.time.LocalDateTime
import java.util.UUID


sealed class LibraryItem(
    val id: UUID,
    val baseUrl: String,
    val playedPercentage: Float,
    val playbackPositionTicks: Long
) {
    class Movie(
        id: UUID,
        playedPercentage: Double = 0.0,
        baseUrl: String,
        playbackPositionTicks: Long = 0L
    ) : LibraryItem(
        id = id,
        baseUrl = baseUrl,
        playedPercentage = playedPercentage.toFloat(),
        playbackPositionTicks = playbackPositionTicks
    ) { companion object }

    class Series(
        id: UUID,
        playedPercentage: Double = 0.0,
        baseUrl: String,
        playbackPositionTicks: Long
    ) : LibraryItem(
        id = id,
        baseUrl = baseUrl,
        playedPercentage = playedPercentage.toFloat(),
        playbackPositionTicks = playbackPositionTicks
    ) { companion object }

    class Episode(
        id: UUID,
        val title: String?,
        val overview: String?,
        val episode: Int,
        val season: Int,
        val hasAired: Boolean,
        val isMissing: Boolean,
        val seriesId: UUID,
        val premiereDate: LocalDateTime,
        val isCompleted: Boolean,
        val runtimeTicks: Long,
        playbackPositionTicks: Long,
        playedPercentage: Float,
        baseUrl: String
    ) : LibraryItem(
        id = id,
        baseUrl = baseUrl,
        playedPercentage = playedPercentage,
        playbackPositionTicks = playbackPositionTicks
    ) { companion object }

    val progress = (playedPercentage / 100)
    val isInProgress = playbackPositionTicks > 0 && playedPercentage > 0f

    companion object
}