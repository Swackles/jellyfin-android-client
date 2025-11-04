package com.swackles.jellyfin.session

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

data class LoginCredentials(
    val hostname: String,
    val username: String,
    val password: String
)

sealed interface AuthState {
    object Loading : AuthState
    data class Authenticated(val session: Session) : AuthState
    object Unauthenticated : AuthState
}

sealed interface SessionEvent {
    object Authenticated: SessionEvent
}


interface SessionManager {
    val authState: StateFlow<AuthState>

    val events: SharedFlow<SessionEvent>

    suspend fun initialize()

    suspend fun getSessions(): List<Session>

    suspend fun login(credentials: LoginCredentials)
}