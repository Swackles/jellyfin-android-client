package com.swackles.jellyfin.session

import java.time.LocalDateTime
import java.util.UUID

data class Session(
    val id: UUID,
    val server: Server,
    val lastActive: LocalDateTime = LocalDateTime.now(),
    val profileImageUrl: String,
    val username: String,
    val token: String
)

data class Server(
    val id: UUID,
    val hostname: String,
    val name: String
)

interface SessionStorage {
    suspend fun updateLastActive(session: Session)

    suspend fun getServers(): List<Server>

    suspend fun getSessionLastUsed(): Session?

    suspend fun save(session: Session)

    suspend fun getSessionsWithServerId(id: UUID): List<Session>

    suspend fun delete(server: Server)

    suspend fun delete(session: Session)
}