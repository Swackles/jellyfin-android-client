package com.swackles.jellyfin.session

data class LoginCredentials(
    val hostname: String,
    val username: String,
    val password: String
)

interface SessionManager {
    suspend fun getSessions(): List<Session>

    val activeSession: Session?

    suspend fun setActiveSession(session: Session)

    suspend fun login(credentials: LoginCredentials)

    suspend fun loginLastSession()
}