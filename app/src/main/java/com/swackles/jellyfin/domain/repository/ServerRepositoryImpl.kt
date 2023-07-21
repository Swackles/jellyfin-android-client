package com.swackles.jellyfin.domain.repository

import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.domain.dao.ServerDao
import com.swackles.jellyfin.domain.models.Server

class ServerRepositoryImpl(private val serverDao: ServerDao) : ServerRepository {
    override val allServers = MutableLiveData<List<Server>>()

    override suspend fun addServer(newServer: Server) = serverDao.addServer(newServer)

    override fun getLastActiveServer() = serverDao.getLastActiveServer()
}