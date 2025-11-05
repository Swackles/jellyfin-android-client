package com.swackles.jellyfin.data

import android.util.Log
import com.swackles.jellyfin.data.dao.ServerDao
import com.swackles.jellyfin.data.dao.ServerEntity
import com.swackles.jellyfin.data.dao.SessionDao
import com.swackles.jellyfin.data.dao.SessionEntity
import com.swackles.jellyfin.session.Server
import com.swackles.jellyfin.session.Session
import com.swackles.jellyfin.session.SessionStorage
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class SessionStorageImpl @Inject constructor(
    val sessionDao: SessionDao,
    val serverDao: ServerDao
): SessionStorage {
    override suspend fun updateLastActive(session: Session) {
        sessionDao.save(session.toEntity().copy(
            lastActive = LocalDateTime.now()
        ))
    }

    override suspend fun getServers(): List<Server> =
        serverDao.getAll().map { it.toModel() }

    override suspend fun getSessionLastUsed(): Session? {
        val session = sessionDao.getLastUsedSession()
            ?: return null

        val server = serverDao.find(id = session.serverId)

        if (server == null) {
            Log.e("StorageImpl", "Session was found with no server attached to it")

            sessionDao.delete(sessionDao.getAllWithServerId(session.serverId))

            return null
        }

        return session.toModel(serverEntity = server)
    }

    override suspend fun save(session: Session) {
        sessionDao.save(session.toEntity())
        serverDao.save(session.server.toEntity())
    }

    override suspend fun getSessionsWithServerId(id: UUID): List<Session> {
        val server = serverDao.find(id) ?: return emptyList()

        return sessionDao.getAllWithServerId(serverId = id)
            .map { it.toModel(serverEntity = server) }
    }

    override suspend fun delete(server: Server) =
        serverDao.delete(entity = server.toEntity())

    override suspend fun delete(session: Session) =
        sessionDao.delete(entity = session.toEntity())

    private fun Server.toEntity(): ServerEntity =
        ServerEntity(
            id = id,
            hostname = hostname,
            name = name
        )

    private fun ServerEntity.toModel() =
        Server(
            id = id,
            hostname = hostname,
            name = name
        )


    private fun Session.toEntity() =
        SessionEntity(
            id = id,
            serverId = server.id,
            lastActive = lastActive,
            profileImageUrl = profileImageUrl,
            username = username,
            token = token
        )

    private fun SessionEntity.toModel(serverEntity: ServerEntity) =
        Session(
            id = this.id,
            server = serverEntity.toModel(),
            lastActive = lastActive,
            username = this.username,
            profileImageUrl = profileImageUrl,
            token = this.token
        )
}