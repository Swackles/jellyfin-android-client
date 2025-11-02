package com.swackles.libs.jellyfin

import org.jellyfin.sdk.model.api.BaseItemPerson
import org.jellyfin.sdk.model.api.PersonKind
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Episode(
    override val id: UUID,
    override val baseUrl: String,
    val title: String,
    val season: Int,
    val episode: Int,
    val playbackPositionTicks: Long,
    val isMissing: Boolean,
    val overview: String,
    val premiereDate: LocalDateTime,
    val runtimeTicks: Long,
    val playedPercentage: Float
): JellyfinItem {
    fun hasBeenPlayed(): Boolean =
        playedPercentage > 0f

    fun hasFinished(): Boolean =
        playedPercentage == 1f

    fun isInProgress(): Boolean =
        hasBeenPlayed() && !hasFinished()
}

sealed class MediaItem(
    override open val id: UUID,
    open val overview: String?,
    open val genres: List<String>,
    open val rating: String?,
    override open val baseUrl: String,
    open val premiereDate: LocalDate,
    open val runTimeTicks: Long = 0L,
    internal open val people: List<BaseItemPerson>
): JellyfinItem {
    class Movie(
        override val id: UUID,
        override val overview: String?,
        override val genres: List<String>,
        override val rating: String?,
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
        people = people,
        premiereDate = premiereDate,
        baseUrl = baseUrl,
        runTimeTicks = runTimeTicks
    ) {
        fun isInProgress(): Boolean =
            playbackPositionTicks > 0 && playedPercentage > 0f
    }

    data class Series(
        override val id: UUID,
        override val overview: String?,
        override val genres: List<String>,
        override val rating: String?,
        override val people: List<BaseItemPerson>,
        override val baseUrl: String,
        override val runTimeTicks: Long,
        override val premiereDate: LocalDate, // TODO: Reevaluate if this is useful, cannot think of at this moment
    ): MediaItem(
        id = id,
        overview = overview,
        genres = genres,
        rating = rating,
        people = people,
        premiereDate = premiereDate,
        baseUrl = baseUrl,
        runTimeTicks = runTimeTicks
    )

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

interface MediaClient {
    suspend fun getItem(id: UUID): MediaItem

    suspend fun getEpisodes(id: UUID): Map<Int, List<Episode>>
}
