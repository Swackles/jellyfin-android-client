package com.swackles.jellyfin.domain.models

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import java.util.UUID

interface DetailMedia {
    val id: UUID
    val overview: String
    val similar: List<Media>
    val genres: List<String>
    val rating: String?
    val actors: List<String>
    val directors: List<String>
    val writers: List<String>
    val producers: List<String>
    val playedPercentage: Float
    val isMovie: Boolean
    val isSeries: Boolean
    val isInProgress: Boolean

    fun getSeasons(): List<Int>
    fun getEpisodes(): Map<Int, List<EpisodeMedia>>
    fun getPosterImageHeight(width: Dp): Dp
    fun getPosterImageWidth(height: Dp): Dp
    fun getBackdropUrl(density: Density, width: Dp? = null, height: Dp? = null): String
    fun getLogoUrl(density: Density, width: Dp? = null, height: Dp? = null): String
    fun getInfo(): List<String>
    fun getDurationString(): String
    fun getProgressBarLabels(): List<DetailMediaBarLabels>
}