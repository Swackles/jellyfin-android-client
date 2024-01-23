package com.swackles.jellyfin.data.repository

import com.swackles.jellyfin.common.Holder
import com.swackles.jellyfin.data.enums.JellyfinResponses
import com.swackles.jellyfin.data.enums.MediaItemType
import com.swackles.jellyfin.data.models.DetailMediaBase
import com.swackles.jellyfin.data.models.EpisodeMedia
import com.swackles.jellyfin.data.models.MediaSection
import com.swackles.jellyfin.data.models.DetailMediaMovie
import com.swackles.jellyfin.data.models.DetailMediaSeries
import com.swackles.jellyfin.data.models.GetMediaFilters
import com.swackles.jellyfin.data.models.Media
import com.swackles.jellyfin.data.models.MediaFilters
import com.swackles.jellyfin.data.models.toMedia
import com.swackles.jellyfin.data.models.toMediaFilter
import org.jellyfin.sdk.api.client.exception.InvalidStatusException
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind.MOVIE
import org.jellyfin.sdk.model.api.BaseItemKind.SERIES
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val api: JellyfinRepository
) : MediaRepository {
    override suspend fun getMedia(id: UUID): Holder<DetailMediaBase> {
        try {
            val media = api.getItem(id)

            if (media.type === SERIES) {
                return Holder.Success(
                    DetailMediaSeries(
                    media,
                    api.getSimilar(id).map { it.toMedia(api.getBaseUrl()) },
                    getEpisodesForMedia(media),
                    api.getBaseUrl())
                )
            } else if (media.type === MOVIE) {
                return Holder.Success(
                    DetailMediaMovie(
                    media,
                    api.getSimilar(id).map { it.toMedia(api.getBaseUrl()) },
                    api.getBaseUrl())
                )
            }

            throw RuntimeException("Media type of \"${media.type}\" is unmapped")
        } catch (ex: InvalidStatusException) {
            return handleError(ex)
        }
    }

    override suspend fun getMediaItems(filters: GetMediaFilters): List<Media> {
        return api.getItems(filters).map { it.toMedia(api.getBaseUrl()) }
    }

    override suspend fun getContinueWatching(): MediaSection {
        return MediaSection(
            "Continue watching",
            api.getContinueWatching().map { item -> item.toMedia(api.getBaseUrl()) }
        )
    }

    override suspend fun getNewlyAdded(): MediaSection {
        return MediaSection(
            "Newly added",
            api.getNewlyAdded().map { item -> item.toMedia(api.getBaseUrl()) }
        )
    }

    override suspend fun getRecommended(): MediaSection {
        return MediaSection(
            "My favorites",
            api.getFavorites().map { item -> item.toMedia(api.getBaseUrl()) }
        )
    }

    override suspend fun getFilters(items: Collection<MediaItemType>): MediaFilters =
        api.getFilters(
            items.map {
                when (it) {
                    MediaItemType.SERIES -> SERIES
                    MediaItemType.MOVIE -> MOVIE
                }
            }
        ).toMediaFilter()

    private suspend fun getEpisodesForMedia(media: BaseItemDto): List<EpisodeMedia> {
        return api.getEpisodes(media.id)
            .map { EpisodeMedia(api.getItem(it.id), api.getBaseUrl()) }
    }

    private fun <T>handleError(ex: InvalidStatusException): Holder<T> {
        return when (ex.status) {
            401 -> Holder.Error(JellyfinResponses.UNAUTHORIZED_RESPONSE.name)
            400 -> {
                println("Error ${ex.status} with msg ${ex.message}")

                return Holder.Error(JellyfinResponses.BAD_REQUEST.name)
            }
            else -> {
                throw ex
            }
        }
    }
}