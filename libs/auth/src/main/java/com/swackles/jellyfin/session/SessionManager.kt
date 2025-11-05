package com.swackles.jellyfin.session

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

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

sealed interface SessionEvent {
    object Authenticated: SessionEvent
    object LoggedOut: SessionEvent
}


interface SessionManager {
    val authState: StateFlow<AuthState>

    val serversState: StateFlow<List<Server>>

    val events: SharedFlow<SessionEvent>

    suspend fun initialize()

    suspend fun getSessions(): List<Session>

    suspend fun login(credentials: LoginCredentials)

    suspend fun logoutActiveServer()

    suspend fun logoutActiveSession()
}