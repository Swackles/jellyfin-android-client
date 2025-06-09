package com.swackles.jellyfin.data.jellyfin.models

import org.jellyfin.sdk.model.api.BaseItemPerson
import org.jellyfin.sdk.model.api.PersonKind
import java.time.LocalDate
import java.util.UUID

sealed class MediaItem(
    open val id: UUID,
    open val overview: String?,
    open val genres: List<String>,
    open val rating: String?,
    open val similar: List<LibraryItem>,
    open val baseUrl: String,
    open val primaryImageAspectRatio: Float,
    open val premiereDate: LocalDate,
    open val runTimeTicks: Long = 0L,
    internal open val people: List<BaseItemPerson>
) {

    class Movie(
        override val id: UUID,
        override val overview: String?,
        override val genres: List<String>,
        override val rating: String?,
        override val similar: List<LibraryItem>,
        override val primaryImageAspectRatio: Float,
        override val people: List<BaseItemPerson>,
        override val baseUrl: String,
        override val premiereDate: LocalDate,
        override val runTimeTicks: Long,
        val playbackPositionTicks: Long,
        val playedPercentage: Float,
    ): MediaItem(
        id = id,
        overview = overview,
        genres = genres,
        rating = rating,
        similar = similar,
        primaryImageAspectRatio = primaryImageAspectRatio,
        people = people,
        premiereDate = premiereDate,
        baseUrl = baseUrl,
        runTimeTicks = runTimeTicks
    ) {

        fun isInProgress(): Boolean =
            playbackPositionTicks > 0 && playedPercentage > 0f

        companion object
    }

    data class Series(
        override val id: UUID,
        override val overview: String?,
        override val genres: List<String>,
        override val rating: String?,
        override val primaryImageAspectRatio: Float,
        override val similar: List<LibraryItem>,
        override val people: List<BaseItemPerson>,
        override val baseUrl: String,
        override val runTimeTicks: Long,
        override val premiereDate: LocalDate, // TODO: Reevaluate if this is useful, cannot think of at this moment
        val episodes: List<LibraryItem.Episode>,
    ): MediaItem(
        id = id,
        overview = overview,
        genres = genres,
        rating = rating,
        similar = similar,
        primaryImageAspectRatio = primaryImageAspectRatio,
        people = people,
        premiereDate = premiereDate,
        baseUrl = baseUrl,
        runTimeTicks = runTimeTicks
    ) {
        fun seasons(): List<Int> =
            episodes.map { it.episode }.distinct().sorted()

        fun groupEpisodeBySeason(): Map<Int, List<LibraryItem.Episode>> =
            episodes.groupBy { it.season }

        companion object
    }

    fun getActors(): List<String> =
        getPeopleByType(PersonKind.ACTOR)

    fun getDirectors(): List<String> =
        getPeopleByType(PersonKind.DIRECTOR)

    fun getWriters(): List<String> =
        getPeopleByType(PersonKind.WRITER)

    fun getProducers(): List<String> =
        getPeopleByType(PersonKind.PRODUCER)

    private fun getPeopleByType(type: PersonKind): List<String> =
        people.filter { it.type == type && it.name != null }.map { it.name!! }
}