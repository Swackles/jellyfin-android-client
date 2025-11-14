package com.swackles.jellyfin.session.inter

import android.content.Context
import android.util.Log
import com.swackles.jellyfin.session.AuthState
import com.swackles.jellyfin.session.LoginCredentials
import com.swackles.jellyfin.session.LogoutScope
import com.swackles.jellyfin.session.Server
import com.swackles.jellyfin.session.Session
import com.swackles.jellyfin.session.SessionEvent
import com.swackles.jellyfin.session.SessionManager
import com.swackles.jellyfin.session.SessionStorage
import com.swackles.jellyfin.session.inter.di.JellyfinProviderFactory
import com.swackles.libs.jellyfin.JellyfinCredentials
import com.swackles.libs.jellyfin.JellyfinUser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import java.util.UUID

internal class SessionManagerImpl(
    val context: Context,
    val sessionStorage: SessionStorage
): SessionManager {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _servers = MutableStateFlow<List<Server>>(emptyList())
    override val serversState: StateFlow<List<Server>> = _servers.asStateFlow()

    private val _events = MutableSharedFlow<SessionEvent>(replay = 0)
    override val events: SharedFlow<SessionEvent> = _events.asSharedFlow()

    override suspend fun initialize() {
        Log.d("SessionManagerImpl", "Initializing session manager")

        val session = sessionStorage.getSessionLastUsed()

        if (session == null) {
            Log.d("SessionManagerImpl", "No session found, exiting initalization")

            _authState.value = AuthState.Unauthenticated
        } else {
            Log.d("SessionManagerImpl", "Found session $session and trying to log in")

            login(LoginCredentials.ExistingSession(session) as LoginCredentials)
        }
    }

    override suspend fun findServer(serverId: UUID): Server? =
        sessionStorage.getServers().first { it.id == serverId }

    override suspend fun getServers(): List<Server> =
        sessionStorage.getServers()

    override suspend fun getSessions(): List<Session> {
        val state = authState.value

        return when(state) {
            is AuthState.Authenticated -> sessionStorage.getSessionsWithServerId(state.session.server.id)
            else -> emptyList()
        }
    }

    override suspend fun getSessions(server: Server): List<Session> =
        sessionStorage.getSessionsWithServerId(server.id)

    override suspend fun login(credentials: LoginCredentials) {
        Log.d("SessionManagerImpl", "Logging in using $credentials")

        val session = when(credentials) {
            is LoginCredentials.ExistingSession ->
                login(credentials)
            is LoginCredentials.ExistingServer ->
                login(credentials)
            is LoginCredentials.NewServer ->
                login(credentials)
        }

        Log.d("SessionManagerImpl", "Logging in success")

        _authState.value = AuthState.Authenticated(session)
        _events.emit(SessionEvent.Authenticated)
    }

    private suspend fun login(credentials: LoginCredentials.ExistingSession): Session {
        login(credentials.toJellyfinCredentials())

        sessionStorage.updateLastActive(credentials.session)

        return credentials.session
    }

    private suspend fun login(credentials: LoginCredentials.NewServer): Session {
        val user = login(credentials.toJellyfinCredentials())

        val session = Session(
            id = UUID.randomUUID(),
            server = Server(
                id = UUID.randomUUID(),
                hostname = credentials.hostname,
                name = user.serverName
            ),
            lastActive = LocalDateTime.now(),
            username = credentials.username,
            profileImageUrl = user.getProfileImage(),
            token = user.token
        )

        sessionStorage.save(session)

        return session
    }

    private suspend fun login(credentials: LoginCredentials.ExistingServer): Session {
        val user = login(credentials.toJellyfinCredentials())

        val session = Session(
            id = UUID.randomUUID(),
            server = credentials.server,
            lastActive = LocalDateTime.now(),
            username = user.username,
            profileImageUrl = user.getProfileImage(),
            token = user.token
        )

        sessionStorage.save(session)

        return session
    }

    private fun LoginCredentials.toJellyfinCredentials(): JellyfinCredentials =
        when(this) {
            is LoginCredentials.ExistingSession ->
                JellyfinCredentials.Existing(
                    hostname = this.session.server.hostname,
                    token = this.session.token
                )
            is LoginCredentials.ExistingServer ->
                JellyfinCredentials.New(
                    hostname = this.server.hostname,
                    username = this.username,
                    password = this.password
                )
            is LoginCredentials.NewServer ->
                JellyfinCredentials.New(
                    hostname = this.hostname,
                    username = this.username,
                    password = this.password
                )
        }

    private suspend fun login(credentials: JellyfinCredentials): JellyfinUser =
        JellyfinProviderFactory.login(context, credentials)
            .userClient.currentUser()

    override suspend fun logoutActiveServer() {
        ensureLoggedIn { session ->
            val server = session.server

            val sessions = sessionStorage.getSessionsWithServerId(server.id)

            sessions.forEach {
                sessionStorage.delete(it)
            }

            sessionStorage.delete(server)

            JellyfinProviderFactory.logOut(context)
            _authState.value = AuthState.Unauthenticated
            _events.emit(SessionEvent.LoggedOut(LogoutScope.Server))
        }
    }

    override suspend fun logoutActiveSession() {
        ensureLoggedIn { session ->
            sessionStorage.delete(session)

            val sessions = sessionStorage.getSessionsWithServerId(session.server.id)

            var scope: LogoutScope = LogoutScope.User(serverId = session.server.id)
            if (sessions.isEmpty()) {
                sessionStorage.delete(session.server)
                scope = LogoutScope.Server
            }

            JellyfinProviderFactory.logOut(context)
            _authState.value = AuthState.Unauthenticated
            _events.emit(SessionEvent.LoggedOut(scope))
        }
    }

    private suspend fun ensureLoggedIn(block: suspend (Session) -> Unit) {
        val state = authState.value
        when(state) {
            is AuthState.Authenticated -> block(state.session)
            else -> Log.e("SessionManagerImpl", "Auth state $state cannot be signed out of")
        }
    }
}
