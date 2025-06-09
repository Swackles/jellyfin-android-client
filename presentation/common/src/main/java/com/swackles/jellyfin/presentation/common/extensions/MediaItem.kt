package com.swackles.jellyfin.presentation.common.extensions

import android.net.Uri
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.swackles.jellyfin.data.jellyfin.enums.ImageType
import com.swackles.jellyfin.data.jellyfin.models.MediaItem

fun MediaItem.getPosterImageHeight(width: Dp) =
    width / primaryImageAspectRatio

fun MediaItem.getPosterImageWidth(height: Dp) =
    height * primaryImageAspectRatio
fun MediaItem.getBackdropUrl(density: Density, width: Dp) =
    getImageUrl(ImageType.BACKDROP, density, width, null)
fun MediaItem.getLogoUrl(density: Density, width: Dp?) =
    getImageUrl(ImageType.LOGO, density, width, null)

private fun MediaItem.getImageUrl(type: ImageType, density: Density, width: Dp?, height: Dp?): String {
    val uriBuilder = Uri.parse(baseUrl)
        .buildUpon()
        .appendPath("/items/$id/images/${type.name}/0")
        .appendQueryParameter("quality", "10")

    if (width != null) uriBuilder.appendQueryParameter("fillWidth", with(density) { width.toPx().toInt() }.toString())
    if (height != null) uriBuilder.appendQueryParameter("fillHeight", with(density) { height.toPx().toInt() }.toString())


    return uriBuilder.build().toString()
}
