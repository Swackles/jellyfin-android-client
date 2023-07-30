package com.swackles.jellyfin.data.repository

import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.data.models.Server
import kotlinx.coroutines.flow.flow

class ServerRepositoryPreview : ServerRepository {
    override val allServers = MutableLiveData<List<Server>>()

    override suspend fun addServer(newServer: Server) {}

    override fun getLastActiveServer() = flow { emit(null) }
}