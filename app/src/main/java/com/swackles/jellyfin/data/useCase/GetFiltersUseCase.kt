package com.swackles.jellyfin.data.useCase

import com.swackles.jellyfin.data.jellyfin.models.Holder
import com.swackles.jellyfin.data.jellyfin.models.MediaFilters
import com.swackles.jellyfin.data.jellyfin.repository.MediaRepository
import com.swackles.jellyfin.domain.common.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.RuntimeException
import javax.inject.Inject

class GetFiltersUseCase @Inject constructor(private val repository: MediaRepository) :
    BaseUseCase<MediaFilters> {
    override operator fun invoke(): Flow<Holder<MediaFilters>> = flow {
        try {
            emit(Holder.Loading())

            emit(Holder.Success(repository.getFilters()))
        } catch (e: RuntimeException) {
            emit(Holder.Error(e.message ?: "Unexpected error"))
            throw (e)
        }
    }
}