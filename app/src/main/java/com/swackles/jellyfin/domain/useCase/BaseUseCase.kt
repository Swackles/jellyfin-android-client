package com.swackles.jellyfin.domain.useCase

import com.swackles.jellyfin.common.Holder
import kotlinx.coroutines.flow.Flow

interface BaseUseCase<T> {
    operator fun invoke(): Flow<Holder<T>>
}