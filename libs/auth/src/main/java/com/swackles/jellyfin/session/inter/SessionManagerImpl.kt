package com.swackles.jellyfin.session.inter

import android.content.Context
import android.util.Log
import com.swackles.jellyfin.session.AuthState
import com.swackles.jellyfin.session.LoginCredentials
import com.swackles.jellyfin.session.Session
import com.swackles.jellyfin.session.SessionEvent
import com.swackles.jellyfin.session.SessionManager
import com.swackles.jellyfin.session.SessionStorage
import com.swackles.jellyfin.session.inter.di.JellyfinProviderFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

internal class SessionManagerImpl(
    val context: Context,
    val storage: SessionStorage
): SessionManager {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _events = MutableSharedFlow<SessionEvent>(replay = 0)
    override val events: SharedFlow<SessionEvent> = _events.asSharedFlow()

    override suspend fun initialize() {
        val session = runCatching { getSessions().last() }
            .getOrElse { err ->
                if (err !is NoSuchElementException) throw err

                Log.d("SessionManagerImpl", "No session found, exiting initalization")

                _authState.value = AuthState.Unauthenticated

                return
            }

        Log.d("SessionManagerImpl", "Found session $session and trying to log in")

        JellyfinProviderFactory
            .login(context, session.hostname, session.token)

        Log.d("SessionManagerImpl", "Session logged in successfully")
        _authState.value = AuthState.Authenticated(session)
    }


    override suspend fun getSessions(): List<Session> =
        storage.getAllSessions()

    override suspend fun login(credentials: LoginCredentials) {
        Log.d("SessionManagerImpl", "Trying to log in credentials $credentials")

        val client = JellyfinProviderFactory
            .login(context, credentials.hostname, credentials.username, credentials.password)

        Log.d("SessionManagerImpl", "Login success")

        val session = Session(
            id = UUID.randomUUID(),
            hostname = credentials.hostname,
            username = credentials.username,
            token = client.jellyfinUser.token
        )

        storage.saveSession(session)
        _authState.value = AuthState.Authenticated(session)

        _events.emit(SessionEvent.Authenticated)
    }
}
