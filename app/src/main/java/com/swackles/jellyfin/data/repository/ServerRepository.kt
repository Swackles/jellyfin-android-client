package com.swackles.jellyfin.data.repository

import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.data.models.Server

interface ServerRepository {
    val allServers: MutableLiveData<List<Server>>

    suspend fun insertOrUpdate(newServer: Server): Long
}
