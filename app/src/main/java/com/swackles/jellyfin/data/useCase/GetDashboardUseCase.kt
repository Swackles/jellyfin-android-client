package com.swackles.jellyfin.data.useCase

import com.swackles.jellyfin.common.Holder
import com.swackles.jellyfin.data.models.MediaSection
import com.swackles.jellyfin.data.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.RuntimeException
import javax.inject.Inject

class GetDashboardUseCase @Inject constructor(private val repository: MediaRepository) :
    BaseUseCase<List<MediaSection>> {
    override operator fun invoke(): Flow<Holder<List<MediaSection>>> = flow {
        println("GetDashboardUseCase invoke")
        try {
            emit(Holder.Loading())
            val mediaSection = listOf(
                repository.getContinueWatching(),
                repository.getNewlyAdded(),
                repository.getRecommended()
            )

            emit(Holder.Success(mediaSection))
        } catch (e: RuntimeException) {
            emit(Holder.Error(e.message ?: "Unexpected error"))
            throw (e)
        }
    }
}