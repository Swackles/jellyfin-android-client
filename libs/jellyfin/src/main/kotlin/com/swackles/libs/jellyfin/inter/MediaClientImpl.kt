package com.swackles.libs.jellyfin.inter

import com.swackles.libs.jellyfin.Episode
import com.swackles.libs.jellyfin.MediaClient
import com.swackles.libs.jellyfin.MediaItem
import com.swackles.libs.jellyfin.PlaybackMetadata
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.Response
import org.jellyfin.sdk.api.client.exception.InvalidStatusException
import org.jellyfin.sdk.api.client.extensions.mediaInfoApi
import org.jellyfin.sdk.api.client.extensions.tvShowsApi
import org.jellyfin.sdk.api.client.extensions.userLibraryApi
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemDtoQueryResult
import org.jellyfin.sdk.model.api.BaseItemKind
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID


class MediaClientImpl(
    private val jellyfinClient: ApiClient
): MediaClient {
        override suspend fun getItem(id: UUID): MediaItem =
            jellyfinClient
                .userLibraryApi.getItem(itemId = id)
                .toMediaItem(jellyfinClient)

    override suspend fun getEpisodes(id: UUID): Map<Int, List<Episode>> = try {
        jellyfinClient.tvShowsApi.getEpisodes(
            seriesId = id
        ) .mapToEpisodes(jellyfinClient.baseUrl!!)
            .groupBy { it.season }
    } catch (err: InvalidStatusException) {
        if (err.status != 404) throw err

        emptyMap()
    }

    private fun Response<BaseItemDtoQueryResult>.mapToEpisodes(baseUrl: String): List<Episode> =
        this.content.items.map { it.toEpisode(baseUrl) }

    private fun BaseItemDto.toEpisode(baseUrl: String): Episode =
        Episode(
            id = id,
            baseUrl = baseUrl,
            title = name ?: "",
            season = parentIndexNumber ?: -1,
            episode = indexNumber ?: -1,
            playbackPositionTicks = userData?.playbackPositionTicks ?: 0L,
            isMissing = runTimeTicks == null,
            overview = overview ?: "",
            premiereDate = premiereDate ?: LocalDateTime.now(),
            runtimeTicks = runTimeTicks ?: 0L,
            playedPercentage = this.playedPercentage()
        )

    private fun Response<BaseItemDto>.toMediaItem(jellyfinClient: ApiClient): MediaItem =
        when (this.content.type) {
            BaseItemKind.MOVIE -> MediaItem.Movie(
                id = content.id,
                overview = content.overview,
                genres = content.genres ?: emptyList(),
                rating = content.officialRating,
                people = content.people ?: emptyList(),
                baseUrl = jellyfinClient.baseUrl!!,
                premiereDate = content.premiereDate?.toLocalDate() ?: LocalDate.now(),
                runTimeTicks = content.runTimeTicks ?: 0L,
                playbackPositionTicks = content.runTimeTicks ?: 0L,
                playedPercentage = content.playedPercentage()
            )
            BaseItemKind.SERIES -> MediaItem.Series(
                id = content.id,
                overview = content.overview,
                genres = content.genres ?: emptyList(),
                rating = content.officialRating,
                people = content.people ?: emptyList(),
                baseUrl = jellyfinClient.baseUrl!!,
                runTimeTicks = content.runTimeTicks ?: 0L,
                premiereDate = content.premiereDate?.toLocalDate() ?: LocalDate.now()
            )
            else -> throw RuntimeException("Unknown media type \"${content.type}\"")
        }

    override suspend fun getPlaybackMetadata(id: UUID): PlaybackMetadata {
        // TODO: Figure out how to determine if it's series or movie

        return PlaybackMetadata.Movie(
            id = id,
            metadata = jellyfinClient.mediaInfoApi.getPlaybackInfo(id).content,
            baseUrl = jellyfinClient.baseUrl!!
        )
    }

    private fun BaseItemDto.playedPercentage(): Float {
        return if (this.userData?.played == true) 1f
        else this.userData?.playedPercentage?.toFloat() ?: .0f
    }
}