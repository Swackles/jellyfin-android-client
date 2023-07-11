package com.swackles.jellyfin.domain.repository

import com.swackles.jellyfin.data.repository.JellyfinRepository
import com.swackles.jellyfin.domain.models.DetailMedia
import com.swackles.jellyfin.domain.models.EpisodeMedia
import com.swackles.jellyfin.domain.models.MediaSection
import com.swackles.jellyfin.domain.models.toDetailMedia
import com.swackles.jellyfin.domain.models.toEpisode
import com.swackles.jellyfin.domain.models.toMedia
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind.SERIES
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val api: JellyfinRepository
) : MediaRepository {
    override suspend fun getMedia(id: UUID): DetailMedia {
        val media = api.getItem(id)
        val similar = api.getSimilar(id)
        val episodes = getEpisodesForMedia(media)

        return media.toDetailMedia(
            baseUrl = api.getBaseUrl(),
            similar = similar.map { it.toMedia(api.getBaseUrl())},
            episodes = episodes,
        )
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

    private suspend fun getEpisodesForMedia(media: BaseItemDto): List<EpisodeMedia> {
        if (media.type !== SERIES) return emptyList()

        return api.getEpisodes(media.id)
            .map { api.getItem(it.id).toEpisode(api.getBaseUrl()) }
    }
}