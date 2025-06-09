package com.swackles.jellyfin.data.jellyfin.mappers

import com.swackles.jellyfin.data.jellyfin.models.LibraryItem
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.Response
import org.jellyfin.sdk.model.DateTime
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemDtoQueryResult
import org.jellyfin.sdk.model.api.BaseItemKind


fun BaseItemDto.toLibraryItem(baseUrl: String): LibraryItem =
    when (type) {
        BaseItemKind.MOVIE -> LibraryItem.Movie(
            id = id,
            playedPercentage = userData?.playedPercentage ?: 0.0,
            baseUrl = baseUrl,
            playbackPositionTicks = userData?.playbackPositionTicks ?: 0L
        )
        BaseItemKind.SERIES -> LibraryItem.Series(
            id = id,
            playedPercentage = userData?.playedPercentage ?: 0.0,
            baseUrl = baseUrl,
            playbackPositionTicks = userData?.playbackPositionTicks ?: 0L
        )
        BaseItemKind.EPISODE -> LibraryItem.Episode(
            id = id,
            title = name,
            overview = overview,
            episode = indexNumber!!,
            season = parentIndexNumber!!,
            hasAired = (premiereDate ?: DateTime.now()) < DateTime.now(),
            isMissing = runTimeTicks == null,
            playedPercentage = userData?.playedPercentage?.toFloat() ?: 0.0F,
            seriesId = seriesId!!,
            playbackPositionTicks = userData?.playbackPositionTicks ?: 0L,
            premiereDate = premiereDate ?: DateTime.now(),
            baseUrl = baseUrl,
            isCompleted = userData?.played ?: false,
            runtimeTicks = runTimeTicks ?: 0L,
        )
        else -> throw RuntimeException("Unknown media type \"$type\"")
    }

internal fun Response<BaseItemDtoQueryResult>.mapToLibraryItems(jellyfinClient: ApiClient): List<LibraryItem> =
    this.content.items.map { it.toLibraryItem(jellyfinClient.baseUrl!!) }