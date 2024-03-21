package com.swackles.jellyfin.data.room.server

import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.data.room.models.Server

class ServerRepositoryPreview : ServerRepository {
    override val allServers = MutableLiveData<List<Server>>()

    override suspend fun insertOrUpdate(newServer: Server): Long = 1
}