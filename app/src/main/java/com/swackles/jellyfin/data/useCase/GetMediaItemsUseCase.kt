package com.swackles.jellyfin.data.useCase

import com.swackles.jellyfin.data.jellyfin.JellyfinClient
import com.swackles.jellyfin.data.jellyfin.models.LibraryItem
import com.swackles.jellyfin.data.jellyfin.models.MediaFilters
import com.swackles.jellyfin.domain.common.models.Holder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMediaItemsUseCase @Inject constructor(private val client: JellyfinClient) {
    operator fun invoke(filters: MediaFilters): Flow<Holder<List<LibraryItem>>> = flow {
        try {
            emit(Holder.Loading())

            emit(Holder.Success(client.libraryService.getItems(filters)))
        } catch (e: RuntimeException) {
            emit(Holder.Error(e.message ?: "Unexpected error"))
            throw (e)
        }
    }
}