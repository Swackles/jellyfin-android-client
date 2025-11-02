package com.swackles.libs.jellyfin

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

interface LibraryClient {
    suspend fun getContinueWatching(): List<LibraryItem>

    suspend fun getNewlyAdded(): List<LibraryItem>

    suspend fun getFavorites(): List<LibraryItem>

    suspend fun getRecommended(): List<LibraryItem>

    suspend fun getSimilar(id: UUID): List<LibraryItem>
}