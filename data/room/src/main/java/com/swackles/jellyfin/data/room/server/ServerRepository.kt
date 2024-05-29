package com.swackles.jellyfin.data.room.server

import com.swackles.jellyfin.data.room.models.Server
import kotlinx.coroutines.flow.Flow

interface ServerRepository {
    fun allServers(): Flow<List<Server>>

    suspend fun getServer(id: Long): Server?

    suspend fun insertOrUpdate(newServer: Server): Long
}
