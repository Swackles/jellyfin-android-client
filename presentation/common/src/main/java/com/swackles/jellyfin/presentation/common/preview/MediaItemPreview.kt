package com.swackles.jellyfin.presentation.common.preview

import com.swackles.jellyfin.data.jellyfin.models.LibraryItem
import com.swackles.jellyfin.data.jellyfin.models.MediaItem
import java.time.LocalDate
import java.util.UUID

fun MediaItem.Movie.Companion.preview(
    durationInMinutes: Long = 24,
    playedPercentage: Float = .0f
) =
    MediaItem.Movie(
        id = UUID.randomUUID(),
        overview = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus congue id lectus vitae efficitur. In nec sem quis mauris sodales interdum id ut nisl.",
        genres = listOf("Action", "Adventure", "Science Fiction", "Thriller"),
        rating = "PG-13",
        similar = listOf(
            LibraryItem.preview(), LibraryItem.preview(), LibraryItem.preview(),
            LibraryItem.preview(), LibraryItem.preview(), LibraryItem.preview(),
            LibraryItem.preview(), LibraryItem.preview(), LibraryItem.preview(),
            LibraryItem.preview(), LibraryItem.preview(), LibraryItem.preview()
        ),
        primaryImageAspectRatio = 0.75f,
        people = emptyList(),
        baseUrl = "test-url",
        playedPercentage = playedPercentage * 100,
        premiereDate = LocalDate.now(),
        runTimeTicks = 24,
        playbackPositionTicks = (durationInMinutes * playedPercentage).toLong(),
    )

fun MediaItem.Series.Companion.preview() =
    MediaItem.Series(
        id = UUID.randomUUID(),
        overview = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus congue id lectus vitae efficitur. In nec sem quis mauris sodales interdum id ut nisl.",
        genres = listOf("Action", "Adventure", "Science Fiction", "Thriller"),
        rating = "PG-13",
        similar = listOf(
            LibraryItem.preview(), LibraryItem.preview(), LibraryItem.preview(),
            LibraryItem.preview(), LibraryItem.preview(), LibraryItem.preview(),
            LibraryItem.preview(), LibraryItem.preview(), LibraryItem.preview(),
            LibraryItem.preview(), LibraryItem.preview(), LibraryItem.preview()
        ),
        primaryImageAspectRatio = 0.75f,
        people = emptyList(),
        baseUrl = "test-url",
        premiereDate = LocalDate.now(),
        runTimeTicks = 24,
        episodes = emptyList()
    )