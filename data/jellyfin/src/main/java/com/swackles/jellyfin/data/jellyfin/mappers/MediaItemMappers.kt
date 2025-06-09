package com.swackles.jellyfin.data.jellyfin.mappers

import com.swackles.jellyfin.data.jellyfin.extensions.playedPercentage
import com.swackles.jellyfin.data.jellyfin.models.LibraryItem
import com.swackles.jellyfin.data.jellyfin.models.MediaItem
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.Response
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import java.time.LocalDate

internal fun Response<BaseItemDto>.toMediaItem(jellyfinClient: ApiClient, similar: List<LibraryItem>): MediaItem =
    when (this.content.type) {
        BaseItemKind.MOVIE -> MediaItem.Movie(
            id = content.id,
            overview = content.overview,
            genres = content.genres ?: emptyList(),
            rating = content.officialRating,
            people = content.people ?: emptyList(),
            similar = similar,
            primaryImageAspectRatio = content.primaryImageAspectRatio?.toFloat() ?: .75f,
            playedPercentage = content.playedPercentage(),
            baseUrl = jellyfinClient.baseUrl!!,
            playbackPositionTicks = content.runTimeTicks ?: 0L,
            runTimeTicks = content.runTimeTicks ?: 0L,
            premiereDate = content.premiereDate?.toLocalDate() ?: LocalDate.now()
        )
        BaseItemKind.SERIES -> MediaItem.Series(
            id = content.id,
            overview = content.overview,
            genres = content.genres ?: emptyList(),
            rating = content.officialRating,
            primaryImageAspectRatio = content.primaryImageAspectRatio?.toFloat() ?: .75f,
            similar = similar,
            people = content.people ?: emptyList(),
            baseUrl = jellyfinClient.baseUrl!!,
            episodes = emptyList(),
            runTimeTicks = content.runTimeTicks ?: 0L,
            premiereDate = content.premiereDate?.toLocalDate() ?: LocalDate.now()
        )
        else -> throw RuntimeException("Unknown media type \"${content.type}\"")
    }
