package com.swackles.jellyfin.data.repository

import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.data.dao.ServerDao
import com.swackles.jellyfin.data.models.Server

class ServerRepositoryImpl(private val serverDao: ServerDao) : ServerRepository {
    override val allServers = MutableLiveData<List<Server>>()

    override suspend fun addServer(newServer: Server) = serverDao.addServer(newServer)

    override fun getLastActiveServer() = serverDao.getLastActiveServer()
}