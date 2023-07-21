package com.swackles.jellyfin.domain.repository

import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.domain.models.Server
import kotlinx.coroutines.flow.Flow

interface ServerRepository {
    val allServers: MutableLiveData<List<Server>>

    suspend fun addServer(newServer: Server)

    fun getLastActiveServer(): Flow<Server?>
}
