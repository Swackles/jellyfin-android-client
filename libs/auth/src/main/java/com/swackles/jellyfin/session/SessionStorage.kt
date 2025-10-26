package com.swackles.jellyfin.session

import java.util.UUID

data class Session(
    val id: UUID,
    val hostname: String,
    val username: String,
    val token: String
)

interface SessionStorage {
    suspend fun getAllSessions(): List<Session>

    suspend fun saveSession(session: Session)
}