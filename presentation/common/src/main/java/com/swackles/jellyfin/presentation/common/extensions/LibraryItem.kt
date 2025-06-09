package com.swackles.jellyfin.presentation.common.extensions

import android.net.Uri
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.swackles.jellyfin.data.jellyfin.enums.ImageType
import com.swackles.jellyfin.data.jellyfin.models.LibraryItem
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

fun LibraryItem.Episode.getDurationString(): String {
    var minutes = (runtimeTicks / 600000000.0).roundToInt()
    val hours = kotlin.math.floor(minutes / 60.0).toInt()
    minutes -= hours * 60

    var durationString = ""

    if (hours > 0) durationString += "$hours hours"
    if (minutes > 0) durationString += "$minutes min"

    return durationString
}

fun LibraryItem.Episode.getAiredString(): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM y")

    return "${if (hasAired) "Aired on" else "Airs on"} ${formatter.format(premiereDate)}"
}

fun LibraryItem.Episode.getBackDropImage(density: Density, width: Dp, height: Dp): String =
    Uri.parse(this.baseUrl)
        .buildUpon()
        .appendPath("items")
        .appendPath(if (isMissing) seriesId.toString() else id.toString())
        .appendPath("images")
        .appendPath(if (isMissing) ImageType.BACKDROP.name else ImageType.PRIMARY.name)
        .appendPath("0")
        .appendQueryParameter("fillWidth", with(density) { width.toPx().toInt() }.toString())
        .appendQueryParameter("fillHeight", with(density) { height.toPx().toInt() }.toString())
        .appendQueryParameter("quality", "10")
        .build()
        .toString()

fun LibraryItem.getPosterUrl(density: Density, width: Dp, height: Dp): String =
    Uri.parse(this.baseUrl)
        .buildUpon()
        .appendPath("items")
        .appendPath(id.toString())
        .appendPath("images")
        .appendPath(ImageType.PRIMARY.name)
        .appendPath("0")
        .appendQueryParameter("fillWidth", with(density) { width.toPx().toInt() }.toString())
        .appendQueryParameter("fillHeight", with(density) { height.toPx().toInt() }.toString())
        .build()
        .toString()
