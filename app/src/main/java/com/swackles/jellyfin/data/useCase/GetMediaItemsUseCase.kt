package com.swackles.jellyfin.data.useCase

import com.swackles.jellyfin.common.Holder
import com.swackles.jellyfin.data.models.GetMediaFilters
import com.swackles.jellyfin.data.models.Media
import com.swackles.jellyfin.data.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.RuntimeException
import javax.inject.Inject

class GetMediaItemsUseCase @Inject constructor(private val repository: MediaRepository) {
    operator fun invoke(filters: GetMediaFilters): Flow<Holder<List<Media>>> = flow {
        println("GetMediaItemsUseCase invoke")
        try {
            emit(Holder.Loading())

            emit(Holder.Success(repository.getMediaItems(filters)))
        } catch (e: RuntimeException) {
            emit(Holder.Error(e.message ?: "Unexpected error"))
            throw (e)
        }
    }
}