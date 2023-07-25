package com.swackles.jellyfin.domain.useCase

import com.swackles.jellyfin.common.Holder
import com.swackles.jellyfin.domain.models.DetailMediaBase
import com.swackles.jellyfin.domain.repository.MediaRepository
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