package com.swackles.jellyfin.domain.useCase

import com.swackles.jellyfin.common.Holder
import com.swackles.jellyfin.domain.models.DetailMedia
import com.swackles.jellyfin.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.RuntimeException
import java.util.UUID
import javax.inject.Inject

class GetDetailUseCase @Inject constructor(private val repository: MediaRepository) {
    operator fun invoke(id: UUID): Flow<Holder<DetailMedia>> = flow {
        try {
            emit(Holder.Loading())
            emit(Holder.Success(repository.getMedia(id)))
        } catch (e: RuntimeException) {
            emit(Holder.Error(e.message ?: "Unexpected error"))
            throw (e)
        }
    }
}