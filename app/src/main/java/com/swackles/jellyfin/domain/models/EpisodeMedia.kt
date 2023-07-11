package com.swackles.jellyfin.domain.models

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.ImageType
import kotlin.math.roundToInt

class EpisodeMedia (
    val id: UUID,
    val episode: Int,
    val season: Int,
    val title: String,
    val overview: String,
    private val aspectRatio: Double,
    private val playedPercentage: Double = 0.0,
    private val runTimeTicks: Long,
    private val imageUrl: String
) {
    val isInProgress get() = progress > 0f
    val progress get(): Float = (playedPercentage / 100).toFloat()

    fun getSize(density: Density, size: Dp): Size {
        return Size(
            density = density,
            width = with(density) { (size).toPx().toInt() * aspectRatio }.dp,
            height = size
        )
    }

    fun getImageUrl(size: Size): String {
        val pxWidth = with(size.density) { size.width.toPx().toInt() }
        val pxHeight = with(size.density) { size.height.toPx().toInt() }

        val uri = "$imageUrl?fillWidth=$pxWidth&fillHeight=$pxHeight&quality=10"
        println(uri)

        return uri
    }

    fun getDurationString(): String {
        var minutes = (runTimeTicks / 600000000.0).roundToInt()
        val hours = kotlin.math.floor(minutes / 60.0).toInt()
        minutes -= hours * 60

        return "${hours}h ${minutes}m"
    }

    companion object {
        fun preview(): EpisodeMedia {
            return EpisodeMedia(
                UUID.randomUUID(),
                1,
                1,
                "Title",
                "Lorem Ipsum",
                1.7,
                0.0,
                10000,
                ""
            )
        }
    }
}

data class Size(
    val density: Density,
    val width: Dp,
    val height: Dp
)

fun BaseItemDto.toEpisode(baseUrl: String): EpisodeMedia {
    if (type != BaseItemKind.EPISODE) throw RuntimeException("\"$type\" is not episode type")
    val playedPercentage = if (userData?.played == true) 100.0
        else userData?.playedPercentage ?: 0.0

    println("Overview: ${overview}")

    return EpisodeMedia(
        id = id,
        episode = indexNumber!!,
        season = parentIndexNumber!!,
        title = name!!,
        overview = overview!!,
        playedPercentage = playedPercentage,
        aspectRatio = primaryImageAspectRatio!!,
        runTimeTicks = runTimeTicks!!,
        imageUrl = "$baseUrl/items/$id/images/${ImageType.PRIMARY.name}/0"
    )
}