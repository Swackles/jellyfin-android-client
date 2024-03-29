package com.swackles.jellyfin.data.jellyfin.models

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.ImageType.PRIMARY
import org.jellyfin.sdk.model.api.QueryFiltersLegacy

class Media(
    val id: UUID,
    private val playedPercentage: Double = 0.0,
    private val posterUrl: String
) {
    val isInProgress get() = progress > 0f
    val progress get(): Float = (playedPercentage / 100).toFloat()

    fun getPosterUrl(density: Density, width: Dp, height: Dp): String {
        val pxWidth = with(density) { width.toPx().toInt() }
        val pxHeight = with(density) { height.toPx().toInt() }

        val uri = "$posterUrl?fillWidth=$pxWidth&fillHeight=$pxHeight"

        return uri
    }

    companion object {
        fun preview(): Media {
            return Media(
                UUID.randomUUID(),
                0.0,
                ""
            )
        }
    }
}

fun BaseItemDto.toMedia(baseUrl: String): Media {
    val mediaId = if (type === BaseItemKind.MOVIE || type === BaseItemKind.SERIES) id
    else if (type === BaseItemKind.EPISODE) seriesId
    else throw RuntimeException("Unknown media type \"$type\"")

    return Media(
        id = id,
        playedPercentage = userData?.playedPercentage ?: 0.0,
        posterUrl = "$baseUrl/items/$mediaId/images/${PRIMARY.name}/0"
    )
}

fun QueryFiltersLegacy.toMediaFilter(): MediaFilters {
    return MediaFilters(
        genres = this.genres ?: emptyList(),
        officialRatings = this.officialRatings ?: emptyList(),
        years = this.years ?: emptyList()
    )
}