package com.swackles.libs.jellyfin

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID


sealed interface LibraryItem: JellyfinItem {
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

data class JellyfinFilters(
    val genres: List<String>,
    val tags: List<String>,
    val ratings: List<String>,
    val years: List<Int>
)

typealias MediaItemType = org.jellyfin.sdk.model.api.BaseItemKind

@Parcelize
data class LibraryFilters(
    val genres: List<String> = emptyList(),
    val officialRatings: List<String> = emptyList(),
    val years: List<Int> = emptyList(),
    val mediaTypes: List<MediaItemType> = emptyList(),
    val query: String? = null
): Parcelable

interface LibraryClient {
    suspend fun getFilters(): JellyfinFilters

    suspend fun search(filters: LibraryFilters): List<LibraryItem>

    suspend fun getContinueWatching(): List<LibraryItem>

    suspend fun getNewlyAdded(): List<LibraryItem>

    suspend fun getFavorites(): List<LibraryItem>

    suspend fun getRecommended(): List<LibraryItem>

    suspend fun getSimilar(id: UUID): List<LibraryItem>
}