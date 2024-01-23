package com.swackles.jellyfin.data.useCase

import com.swackles.jellyfin.common.Holder
import com.swackles.jellyfin.data.models.MediaFilters
import com.swackles.jellyfin.data.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.RuntimeException
import javax.inject.Inject

class GetFiltersUseCase @Inject constructor(private val repository: MediaRepository) :
    BaseUseCase<MediaFilters> {
    override operator fun invoke(): Flow<Holder<MediaFilters>> = flow {
        println("GetSearchUseCase invoke")
        try {
            emit(Holder.Loading())

            emit(Holder.Success(repository.getFilters()))
        } catch (e: RuntimeException) {
            emit(Holder.Error(e.message ?: "Unexpected error"))
            throw (e)
        }
    }
}