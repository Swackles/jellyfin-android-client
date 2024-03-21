package com.swackles.jellyfin.data.repository

import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.data.models.Server

class ServerRepositoryPreview : ServerRepository {
    override val allServers = MutableLiveData<List<Server>>()

    override suspend fun insertOrUpdate(newServer: Server): Long = 1
}