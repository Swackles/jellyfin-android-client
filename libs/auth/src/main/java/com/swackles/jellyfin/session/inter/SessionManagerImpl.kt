package com.swackles.jellyfin.session.inter

import android.content.Context
import android.util.Log
import com.swackles.jellyfin.session.LoginCredentials
import com.swackles.jellyfin.session.Session
import com.swackles.jellyfin.session.SessionManager
import com.swackles.jellyfin.session.SessionStorage
import com.swackles.jellyfin.session.inter.di.JellyfinProviderFactory
import java.util.UUID

internal class SessionManagerImpl(
    val context: Context,
    val storage: SessionStorage
): SessionManager {
    override var activeSession: Session? = null
        private set

    override suspend fun getSessions(): List<Session> =
        storage.getAllSessions()

    override suspend fun setActiveSession(session: Session) {
        activeSession = session
    }

    override suspend fun loginLastSession() {
        val sessions = getSessions()

        Log.d("SessionManagerImpl", getSessions().toString())

        if (sessions.isEmpty()) return
        val lastSession = sessions.last()

        JellyfinProviderFactory
            .login(context, lastSession.hostname, lastSession.token)

        activeSession = lastSession
    }

    override suspend fun login(credentials: LoginCredentials) {
        val client = JellyfinProviderFactory
            .login(context, credentials.hostname, credentials.username, credentials.password)

        val session = Session(
            id = UUID.randomUUID(),
            hostname = credentials.hostname,
            username = credentials.username,
            token = client.jellyfinUser.token
        )

        storage.saveSession(session)
        activeSession = session
    }
}
