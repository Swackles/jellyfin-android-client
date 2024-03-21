package com.swackles.jellyfin.data.useCase

import com.swackles.jellyfin.data.jellyfin.models.Holder
import com.swackles.jellyfin.data.jellyfin.models.DetailMediaBase
import com.swackles.jellyfin.data.jellyfin.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class GetDetailUseCase @Inject constructor(private val repository: MediaRepository) {
    operator fun invoke(id: UUID): Flow<Holder<DetailMediaBase>> = flow {
        emit(Holder.Loading())
        emit(repository.getMedia(id))
    }
}