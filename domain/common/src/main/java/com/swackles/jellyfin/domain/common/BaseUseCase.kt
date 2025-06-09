package com.swackles.jellyfin.domain.common

import com.swackles.jellyfin.domain.common.models.Holder
import kotlinx.coroutines.flow.Flow

interface BaseUseCase<T> {
    operator fun invoke(): Flow<Holder<T>>
}