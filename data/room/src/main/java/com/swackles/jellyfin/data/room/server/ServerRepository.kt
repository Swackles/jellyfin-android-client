package com.swackles.jellyfin.data.room.server

import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.data.room.models.Server

interface ServerRepository {
    val allServers: MutableLiveData<List<Server>>

    suspend fun insertOrUpdate(newServer: Server): Long
}
