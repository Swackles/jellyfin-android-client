package com.swackles.jellyfin.data.jellyfin.models

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.ImageType
import org.jellyfin.sdk.model.api.ImageType.BACKDROP
import org.jellyfin.sdk.model.api.ImageType.LOGO
import org.jellyfin.sdk.model.api.PersonKind

abstract class DetailMediaBase(
    private val baseItem: BaseItemDto,
    override val similar: List<Media>,
    private val episodes: List<EpisodeMedia>,
    private val baseUrl: String
) : DetailMedia {
    override val id = baseItem.id
    override val overview = baseItem.overview ?: ""
    override val genres = baseItem.genres ?: emptyList()
    override val rating = baseItem.officialRating
    override val actors = findPeopleNamesOfTypes(PersonKind.ACTOR)
    override val directors = findPeopleNamesOfTypes(PersonKind.DIRECTOR)
    override val writers = findPeopleNamesOfTypes(PersonKind.WRITER)
    override val producers = findPeopleNamesOfTypes(PersonKind.PRODUCER)
    override val isMovie = baseItem.type == BaseItemKind.MOVIE
    override val isSeries = baseItem.type == BaseItemKind.SERIES

    private val primaryImageAspectRatio = baseItem.primaryImageAspectRatio?.toFloat() ?: .75f

    override fun getPosterImageHeight(width: Dp) = (width / primaryImageAspectRatio)
    override fun getPosterImageWidth(height: Dp) = (height * primaryImageAspectRatio)
    override fun getBackdropUrl(density: Density, width: Dp?, height: Dp?) = this.getImageUrl(BACKDROP, density, width, height)
    override fun getLogoUrl(density: Density, width: Dp?, height: Dp?) = this.getImageUrl(LOGO, density, width, height)

    private fun getImageUrl(type: ImageType, density: Density, width: Dp?, height: Dp?): String {
        val args = emptyMap<String, String>()
        if (width != null) args.plus(Pair("fillWidth", with(density) { width.toPx().toInt() }))
        if (height != null) args.plus(Pair("fillHeight", with(density) { height.toPx().toInt() }))

        return "$baseUrl/items/$id/images/${type.name}/0${args.entries.joinToString(prefix = "?", separator = "&")}"
    }

    private fun findPeopleNamesOfTypes(type: PersonKind): List<String> {
        val people = baseItem.people ?: emptyList()

        return people.filter { it.type == type && it.name != null }.map { it.name!! }
    }
}
