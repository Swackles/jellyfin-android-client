package com.swackles.jellyfin.data.room.server

import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.data.room.models.Server

internal class ServerRepositoryImpl(private val serverDao: ServerDao) :
    ServerRepository {
    override val allServers = MutableLiveData<List<Server>>()

    override suspend fun getServer(id: Long): Server? = serverDao.getServer(id)

    override suspend fun insertOrUpdate(newServer: Server): Long {
        serverDao.getServerByHost(newServer.host)?.let {
            return serverDao.insertOrUpdate(newServer.copy(id = it.id))
        }

        return serverDao.insertOrUpdate(newServer)
    }
}