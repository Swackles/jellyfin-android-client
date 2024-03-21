package com.swackles.jellyfin.data.jellyfin.models

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.swackles.jellyfin.data.jellyfin.extensions.durationString
import com.swackles.jellyfin.data.jellyfin.extensions.playedPercentage
import org.jellyfin.sdk.model.DateTime
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.ImageType
import java.time.format.DateTimeFormatter

open class EpisodeMedia (
    private val baseItem: BaseItemDto,
    private val baseUrl: String
) {
    val id = baseItem.id
    val episode = baseItem.indexNumber
    val season = baseItem.parentIndexNumber
    val title = if(hasAired()) "$episode. ${baseItem.name}" else baseItem.name ?: "Episode $episode"
    val overview = baseItem.overview ?: ""
    val playbackPositionTicks = baseItem.userData?.playbackPositionTicks ?: 0

    fun hasAired() = (baseItem.premiereDate ?: DateTime.now()) < DateTime.now()
    fun isMissing() = baseItem.runTimeTicks == null
    fun progress() = (playedPercentage() / 100)
    fun isInProgress() = !isCompleted() && progress() > 0f
    fun getSize(density: Density, size: Dp): Size {
        return Size(
            density = density,
            width = size * 1.8f,
            height = size
        )
    }

    open fun getImageUrl(size: Size): String {
        val pxWidth = with(size.density) { size.width.toPx().toInt() }
        val pxHeight = with(size.density) { size.height.toPx().toInt() }
        val id = getMissingOrExistingVariable(baseItem.seriesId, baseItem.id)
        val type = getMissingOrExistingVariable(ImageType.BACKDROP, ImageType.PRIMARY)

        return "$baseUrl/items/$id/images/$type/0?fillWidth=$pxWidth&fillHeight=$pxHeight&quality=10"
    }

    fun getSubText(): String = getMissingOrExistingVariable(getAiredString(), getDurationString())

    fun getDurationString() = baseItem.durationString()

    fun getAiredString(): String {
        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM y")

        val prefix = if (hasAired()) "Aired on" else "Airs on"
        val date = formatter.format(baseItem.premiereDate ?: DateTime.now())

        return "$prefix $date"
    }

    private fun <T>getMissingOrExistingVariable(missingVar: T, existingVar: T): T =
        if (isMissing()) missingVar else existingVar

    private fun playedPercentage() = baseItem.playedPercentage()
    private fun isCompleted() = baseItem.userData?.played ?: false
}

data class Size(
    val density: Density,
    val width: Dp,
    val height: Dp
)
