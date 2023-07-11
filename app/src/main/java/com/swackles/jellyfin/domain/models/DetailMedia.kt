package com.swackles.jellyfin.domain.models

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import org.jellyfin.sdk.model.DateTime
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.BaseItemPerson
import org.jellyfin.sdk.model.api.ImageType
import org.jellyfin.sdk.model.api.ImageType.BACKDROP
import org.jellyfin.sdk.model.api.ImageType.LOGO
import org.jellyfin.sdk.model.api.MediaStream
import org.jellyfin.sdk.model.api.MediaStreamType
import java.util.UUID.*
import kotlin.math.roundToInt

class DetailMedia(
    val id: UUID,
    val overview: String?,
    val similar: List<Media>,
    val genres: List<String>,
    val rating: String?,
    val actors: List<String>,
    val directors: List<String>,
    val writers: List<String>,
    val producers: List<String>,
    val subtitles: List<String>,
    val audios: List<String>,
    private val episodes: List<EpisodeMedia>,
    private val type: BaseItemKind,
    private val premiereDate: DateTime?,
    private val runTimeTicks: Long?,
    private val baseUrl: String
) {
    val isMovie get() = type == BaseItemKind.MOVIE

    val isSeries get() = type == BaseItemKind.SERIES

    val getEpisodes get() = episodes.groupBy { it.season }

    fun getBackdropUrl(density: Density, width: Dp, height: Dp): String {
        val url =  this.getImageUrl(BACKDROP, density, width, height)

        println("backdrop: $url")
        return url
    }

    fun getLogoUrl(density: Density, width: Dp, height: Dp): String {
        val url = this.getImageUrl(LOGO, density, width, height)

        println("logo: $url")
        return url
    }

    fun getInfo(): List<String?> {
        val infoList = mutableListOf<String?>()

        if (this.premiereDate != null) infoList.addAll(listOf(premiereDate.year.toString(), null))
        infoList.add(getDurationString())
        if (this.genres.isNotEmpty()) infoList.addAll(listOf(null, genres.first()))

        return infoList
    }

    fun getDurationString(): String? {
        if (runTimeTicks == null) return null

        var minutes = (runTimeTicks / 600000000.0).roundToInt()
        val hours = kotlin.math.floor(minutes / 60.0).toInt()
        minutes -= hours * 60

        return "${hours}h ${minutes}m"
    }

    private fun getImageUrl(type: ImageType, density: Density, width: Dp, height: Dp): String {
        val pxWidth = with(density) { width.toPx().toInt() }
        val pxHeight = with(density) { height.toPx().toInt() }

        return "$baseUrl/items/$id/images/${type.name}/0?fillWidth=$pxWidth&fillHeight=$pxHeight"
    }

    companion object {
        fun preview(): DetailMedia {
            return DetailMedia(
                id = randomUUID(),
                overview = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus congue id lectus vitae efficitur. In nec sem quis mauris sodales interdum id ut nisl.",
                genres = listOf("Lorem ipsum"),
                rating = "rating",
                actors = listOf("Actor1", "Actor2", "Actor3"),
                directors = listOf("Director"),
                writers = listOf("Writer1"),
                producers = listOf("Producer1"),
                subtitles = listOf("Subtitle1"),
                audios = listOf("Audio1"),
                premiereDate = DateTime.now(),
                runTimeTicks = 67637534720,
                baseUrl = "test-url",
                type = BaseItemKind.MOVIE,
                episodes = emptyList(),
                similar = listOf(
                    Media.preview(), Media.preview(), Media.preview(),
                    Media.preview(), Media.preview(), Media.preview(),
                    Media.preview(), Media.preview(), Media.preview(),
                    Media.preview(), Media.preview(), Media.preview()
                )
            )
        }
    }
}

fun BaseItemDto.toDetailMedia(baseUrl: String, similar: List<Media>, episodes: List<EpisodeMedia>): DetailMedia {
    val people = this.people ?: emptyList()
    val mediaStreams = this.mediaStreams ?: emptyList()

    println("type: $type")

    return DetailMedia(
        id = id,
        overview = overview,
        similar = similar,
        genres = genres ?: emptyList(),
        rating = officialRating,
        actors = findPeopleNamesOfTypes(people, "Actor"),
        directors = findPeopleNamesOfTypes(people, "Director"),
        writers = findPeopleNamesOfTypes(people, "Writer"),
        producers = findPeopleNamesOfTypes(people, "Producer"),
        subtitles = findMediaStreamLanguages(mediaStreams, MediaStreamType.SUBTITLE),
        audios = findMediaStreamLanguages(mediaStreams, MediaStreamType.AUDIO),
        episodes = episodes,
        premiereDate = premiereDate,
        runTimeTicks = runTimeTicks,
        type = type,
        baseUrl = baseUrl
    )
}

private fun findPeopleNamesOfTypes(people: List<BaseItemPerson>, type: String): List<String> {
    return people.filter { it.type == type && it.name != null }.map { it.name!! }
}

private fun findMediaStreamLanguages(people: List<MediaStream>, type: MediaStreamType): List<String> {
    return people.filter { it.type == type && it.language != null }.map { it.language!! }
}