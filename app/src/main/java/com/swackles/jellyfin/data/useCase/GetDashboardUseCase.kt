package com.swackles.jellyfin.data.useCase

import com.swackles.jellyfin.data.jellyfin.JellyfinClient
import com.swackles.jellyfin.domain.common.BaseUseCase
import com.swackles.jellyfin.domain.common.models.Holder
import com.swackles.jellyfin.presentation.common.models.MediaSection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDashboardUseCase @Inject constructor(private val client: JellyfinClient) :
    BaseUseCase<List<MediaSection>> {
    override operator fun invoke(): Flow<Holder<List<MediaSection>>> = flow {
        try {
            emit(Holder.Loading())

            val mediaSection = listOf(
                MediaSection(
                    title = "Continue Watching",
                    medias = client.libraryService.getContinueWatching()
                ),
                MediaSection(
                    title = "New",
                    medias = client.libraryService.getNewlyAdded()
                ),
                MediaSection(
                    title = "Recommended",
                    medias = client.libraryService.getRecommended()
                )
            )

            emit(Holder.Success(mediaSection))
        } catch (e: RuntimeException) {
            emit(Holder.Error(e.message ?: "Unexpected error"))
            throw (e)
        }
    }
}