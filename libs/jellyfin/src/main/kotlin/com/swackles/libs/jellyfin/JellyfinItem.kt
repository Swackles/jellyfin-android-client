package com.swackles.libs.jellyfin

import androidx.core.net.toUri
import org.jellyfin.sdk.model.api.ImageType
import java.util.UUID

interface JellyfinItem {
    val id: UUID
    val baseUrl: String
}

fun JellyfinItem.getPosterUrl(width: Int, height: Int): String =
    this.getImageUrl(ImageType.PRIMARY, width, height)

fun JellyfinItem.getThumbUrl(width: Int, height: Int): String =
    this.getImageUrl(ImageType.THUMB, width, height)

fun JellyfinItem.getBackDropUrl(width: Int, height: Int): String =
    this.getImageUrl(ImageType.BACKDROP, width, height)

fun JellyfinItem.getLogoUrl(width: Int, height: Int): String =
    this.getImageUrl(ImageType.LOGO, width, height)


private fun JellyfinItem.getImageUrl(imageType: ImageType, width: Int, height: Int) =
    this.baseUrl.toUri()
        .buildUpon()
        .appendPath("items")
        .appendPath(id.toString())
        .appendPath("images")
        .appendPath(imageType.name)
        .appendPath("0")
        .appendQueryParameter("fillWidth", width.toString())
        .appendQueryParameter("fillHeight", height.toString())
        .build()
        .toString()