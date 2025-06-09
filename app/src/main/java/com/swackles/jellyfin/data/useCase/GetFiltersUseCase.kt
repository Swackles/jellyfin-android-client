package com.swackles.jellyfin.data.useCase

import com.swackles.jellyfin.data.jellyfin.JellyfinClient
import com.swackles.jellyfin.data.jellyfin.models.PossibleFilters
import com.swackles.jellyfin.domain.common.BaseUseCase
import com.swackles.jellyfin.domain.common.models.Holder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFiltersUseCase @Inject constructor(private val client: JellyfinClient) :
    BaseUseCase<PossibleFilters> {

    override operator fun invoke(): Flow<Holder<PossibleFilters>> = flow {
        try {
            emit(Holder.Loading())

            emit(Holder.Success(client.libraryService.getFilters()))
        } catch (e: RuntimeException) {
            emit(Holder.Error(e.message ?: "Unexpected error"))
            throw (e)
        }
    }
}