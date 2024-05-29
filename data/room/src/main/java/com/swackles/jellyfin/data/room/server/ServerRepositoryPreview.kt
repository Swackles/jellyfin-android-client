package com.swackles.jellyfin.data.room.server

import com.swackles.jellyfin.data.room.models.Server
import kotlinx.coroutines.flow.flow

class ServerRepositoryPreview : ServerRepository {
    override fun allServers() = flow { emit(emptyList<Server>()) }

    override suspend fun getServer(id: Long): Server? = null

    override suspend fun insertOrUpdate(newServer: Server): Long = 1
}