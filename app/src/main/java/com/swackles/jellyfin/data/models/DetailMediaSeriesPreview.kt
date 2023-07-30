package com.swackles.jellyfin.data.models

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.swackles.jellyfin.R
import org.jellyfin.sdk.model.DateTime
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.BaseItemPerson
import org.jellyfin.sdk.model.api.MediaStream
import org.jellyfin.sdk.model.api.MediaStreamType
import org.jellyfin.sdk.model.api.UserItemDataDto
import java.util.UUID

class DetailMediaSeriesPreview(
    episodes: List<EpisodeMediaPreview> = listOf(
        EpisodeMediaPreview(100f, 1, 1, R.drawable.episode_thumbnail_image_1),
        EpisodeMediaPreview(100f, 2, 1, R.drawable.episode_thumbnail_image_2),
        EpisodeMediaPreview(25f, 3, 1, R.drawable.episode_thumbnail_image_3),
        EpisodeMediaPreview(0f, 4, 1, R.drawable.episode_thumbnail_image_4),
        EpisodeMediaPreview(0f, 5, 1, R.drawable.episode_thumbnail_image_5),
        EpisodeMediaPreview(0f, 1, 2, R.drawable.episode_thumbnail_image_1),
        EpisodeMediaPreview(0f, 2, 2, R.drawable.episode_thumbnail_image_2),
        EpisodeMediaPreview(0f, 3, 2, R.drawable.episode_thumbnail_image_3)
    )
) : DetailMediaSeries(
    baseItem = BaseItemDto(
        id = UUID.randomUUID(),
        type = BaseItemKind.MOVIE,
        premiereDate = DateTime.now(),
        overview = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus congue id lectus vitae efficitur. In nec sem quis mauris sodales interdum id ut nisl.",
        genres = listOf("Lorem ipsum"),
        officialRating = "PG-13",
        people = listOf(
            createActorPerson("Actor1"), createActorPerson("Actor2"), createActorPerson("Actor3"),
            createDirectorPerson("Director1"),
            createWriterPerson("Writer1"), createWriterPerson("Writer2"),
            createProducerPerson("Producer1"),
        ),
        mediaStreams = listOf(
            createSubtitleMedia("Subtitle1"), createSubtitleMedia("Subtitle2"),
            createAudioMedia("Audio1")
        ),
        aspectRatio = "0.75",
        userData = UserItemDataDto(
            isFavorite = true,
            playCount = 0,
            playbackPositionTicks = 0,
            played = false
        )
    ),
    similar = listOf(
        Media.preview(), Media.preview(), Media.preview(),
        Media.preview(), Media.preview(), Media.preview(),
        Media.preview(), Media.preview(), Media.preview(),
        Media.preview(), Media.preview(), Media.preview()
    ),
    episodes,
    baseUrl = "test-url"
) {

    override fun getEpisodes() = emptyMap<Int, List<EpisodeMedia>>()
    override fun getPosterImageHeight(width: Dp) = (width / .75f)
    override fun getPosterImageWidth(height: Dp) = (height * .75f)
    override fun getBackdropUrl(density: Density, width: Dp?, height: Dp?): String = R.drawable.series_poster_image.toString()
    override fun getLogoUrl(density: Density, width: Dp?, height: Dp?): String = R.drawable.series_logo_image.toString()

}

private fun createPerson(name: String, type: String) = BaseItemPerson(
    id = UUID.randomUUID(),
    name = name,
    type = type
)
private fun createActorPerson(name: String) = createPerson(name, "Actor")
private fun createDirectorPerson(name: String) = createPerson(name, "Director")
private fun createWriterPerson(name: String) = createPerson(name, "Writer")
private fun createProducerPerson(name: String) = createPerson(name, "Producer")

private fun createMedia(title: String, type: MediaStreamType) = MediaStream(
    index = 1,
    isDefault = true,
    isExternal = true,
    isForced = true,
    isInterlaced = true,
    isTextSubtitleStream = true,
    supportsExternalStream = true,
    type = type,
    title = title
)
private fun createSubtitleMedia(title: String) = createMedia(title, MediaStreamType.SUBTITLE)
private fun createAudioMedia(title: String) = createMedia(title, MediaStreamType.AUDIO)