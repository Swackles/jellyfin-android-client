package com.swackles.jellyfin.di

import com.swackles.jellyfin.data.dao.SessionDao
import com.swackles.jellyfin.data.dao.SessionEntity
import com.swackles.jellyfin.session.Session
import com.swackles.jellyfin.session.SessionStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SessionProviderModule {
    @Provides
    @Singleton
    fun provideSessionStorage(sessionDao: SessionDao): SessionStorage = object : SessionStorage {
        override suspend fun getAllSessions(): List<Session> = sessionDao.getAll().map { it.toSession() }

        override suspend fun saveSession(session: Session) = sessionDao.insert(session.toEntity())
    }

    private fun Session.toEntity() =
        SessionEntity(
            id = this.id,
            hostname = this.hostname,
            username = this.username,
            token = this.token
        )

    private fun SessionEntity.toSession() =
        Session(
            id = this.id,
            hostname = this.hostname,
            username = this.username,
            token = this.token
        )
}