package com.swackles.jellyfin.data.useCase

import com.swackles.jellyfin.data.jellyfin.JellyfinClient
import com.swackles.jellyfin.data.jellyfin.models.MediaItem
import com.swackles.jellyfin.domain.common.models.Holder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class GetDetailUseCase @Inject constructor(private val client: JellyfinClient) {
    operator fun invoke(id: UUID): Flow<Holder<MediaItem>> = flow {
        emit(Holder.Loading())
        emit(Holder.Success(client.libraryService.getItem(id)))
    }
}
