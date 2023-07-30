package com.swackles.jellyfin.data.repository

import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.data.models.Server
import kotlinx.coroutines.flow.Flow

interface ServerRepository {
    val allServers: MutableLiveData<List<Server>>

    suspend fun addServer(newServer: Server)

    fun getLastActiveServer(): Flow<Server?>
}
