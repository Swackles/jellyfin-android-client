package com.swackles.jellyfin.domain.repository

import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.domain.models.Server
import kotlinx.coroutines.flow.flow

class ServerRepositoryPreview : ServerRepository {
    override val allServers = MutableLiveData<List<Server>>()

    override suspend fun addServer(newServer: Server) {}

    override fun getLastActiveServer() = flow { emit(null) }
}