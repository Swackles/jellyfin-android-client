package com.swackles.libs.jellyfin

import androidx.core.net.toUri
import org.jellyfin.sdk.model.api.ImageType
import java.util.UUID


sealed interface LibraryItem {
    val id: UUID
    val baseUrl: String
    val playedPercentage: Float
    val playbackPositionTicks: Long

    class Movie(
        override val id: UUID,
        override val playedPercentage: Float,
        override val baseUrl: String,
        override val playbackPositionTicks: Long,
        val genres: List<String>
    ) : LibraryItem

    class Series(
        override val id: UUID,
        override val playedPercentage: Float,
        override val baseUrl: String,
        override val playbackPositionTicks: Long,
        val genres: List<String>
    ) : LibraryItem

    class Episode(
        override val id: UUID,
        val seriesTitle: String,
        val title: String,
        val episode: Int,
        val season: Int,
        override val playbackPositionTicks: Long,
        override val playedPercentage: Float,
        override val baseUrl: String
    ) : LibraryItem
}

fun LibraryItem.getPosterUrl(width: Int, height: Int): String =
    this.getImageUrl(ImageType.PRIMARY, width, height)

fun LibraryItem.getThumbUrl(width: Int, height: Int): String =
    this.getImageUrl(ImageType.THUMB, width, height)

fun LibraryItem.getBackDropUrl(width: Int, height: Int): String =
    this.getImageUrl(ImageType.BACKDROP, width, height)

private fun LibraryItem.getImageUrl(imageType: ImageType, width: Int, height: Int) =
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

interface LibraryClient {
    suspend fun getContinueWatching(): List<LibraryItem>

    suspend fun getNewlyAdded(): List<LibraryItem>

    suspend fun getFavorites(): List<LibraryItem>

    suspend fun getRecommended(): List<LibraryItem>
}