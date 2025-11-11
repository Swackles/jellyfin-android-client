package com.swackles.jellyfin.session

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

sealed interface LoginCredentials {
    data class ExistingSession(
        val session: Session
    ): LoginCredentials

    data class ExistingServer(
        val server: Server,
        val username: String,
        val password: String
    ): LoginCredentials

    data class NewServer(
        val hostname: String,
        val username: String,
        val password: String
    ): LoginCredentials
}

sealed interface AuthState {
    object Loading : AuthState
    data class Authenticated(val session: Session) : AuthState
    object Unauthenticated : AuthState
}

sealed interface LogoutScope {
    object Server: LogoutScope
    data class User(val serverId: UUID): LogoutScope
}

sealed interface SessionEvent {
    object Authenticated: SessionEvent
    data class LoggedOut(val scope: LogoutScope): SessionEvent
}


interface SessionManager {
    val authState: StateFlow<AuthState>

    val serversState: StateFlow<List<Server>>

    val events: SharedFlow<SessionEvent>

    suspend fun initialize()

    suspend fun findServer(serverId: UUID): Server?

    suspend fun getSessions(): List<Session>

    suspend fun getSessions(server: Server): List<Session>

    suspend fun login(credentials: LoginCredentials)

    suspend fun logoutActiveServer()

    suspend fun logoutActiveSession()
}