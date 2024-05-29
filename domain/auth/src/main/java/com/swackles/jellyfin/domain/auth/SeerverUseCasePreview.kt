package com.swackles.jellyfin.domain.auth

import com.swackles.jellyfin.data.room.models.Server
import com.swackles.jellyfin.data.room.server.ServerRepositoryPreview
import kotlinx.coroutines.flow.flow

class ServerUseCasePreview: ServerUseCase(
    ServerRepositoryPreview(),
) {
    override val servers = flow { emit(emptyList<Server>()) }
}