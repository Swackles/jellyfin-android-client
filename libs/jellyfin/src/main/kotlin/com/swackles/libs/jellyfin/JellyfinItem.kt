package com.swackles.libs.jellyfin

import androidx.core.net.toUri
import org.jellyfin.sdk.model.api.ImageType
import java.util.UUID

interface JellyfinItem {
    val id: UUID
    val baseUrl: String
}

fun JellyfinItem.getPosterUrl(width: Int, height: Int, quality: Float = .9f): String =
    this.getImageUrl(ImageType.PRIMARY, width, height, quality)

fun JellyfinItem.getThumbUrl(width: Int, height: Int, quality: Float = .9f): String =
    this.getImageUrl(ImageType.THUMB, width, height, quality)

fun JellyfinItem.getBackDropUrl(width: Int, height: Int, quality: Float = .9f): String =
    this.getImageUrl(ImageType.BACKDROP, width, height, quality)

fun JellyfinItem.getLogoUrl(width: Int, height: Int, quality: Float = .9f): String =
    this.getImageUrl(ImageType.LOGO, width, height, quality)

private fun JellyfinItem.getImageUrl(imageType: ImageType, width: Int, height: Int, quality: Float) =
    this.baseUrl.toUri()
        .buildUpon()
        .appendPath("items")
        .appendPath(id.toString())
        .appendPath("images")
        .appendPath(imageType.name)
        .appendPath("0")
        .appendQueryParameter("fillWidth", width.toString())
        .appendQueryParameter("fillHeight", height.toString())
        .appendQueryParameter("quality", (quality * 100).toInt().toString())
        .build()
        .toString()