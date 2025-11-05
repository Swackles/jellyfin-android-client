package com.swackles.jellyfin.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.swackles.jellyfin.data.dao.ServerDao
import com.swackles.jellyfin.data.dao.ServerEntity
import com.swackles.jellyfin.data.dao.SessionDao
import com.swackles.jellyfin.data.dao.SessionEntity
import com.swackles.jellyfin.session.Server
import com.swackles.jellyfin.session.Session
import com.swackles.jellyfin.session.SessionStorage
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class SessionStorageImplTest {
    private lateinit var database: JellyfinDatabase

    private lateinit var sessionDao: SessionDao
    private lateinit var serverDao: ServerDao

    private lateinit var sessionStorage: SessionStorage

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, JellyfinDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        sessionDao = database.sessionDao()
        serverDao = database.serverDao()

        sessionStorage = SessionStorageImpl(
            sessionDao = sessionDao,
            serverDao = serverDao
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    @Ignore("TODO: Re-enable when mockk gets updated to 1.14.5")
    fun updateLastActive_shouldUpdateLastActiveField_whenExistingSession(): Unit = runBlocking {
        mockkStatic(LocalDateTime::class)
        val dateTime = LocalDateTime.of(2020, 1, 1, 0, 0)
        every { LocalDateTime.now() } returns dateTime

        val sessionEntity = createSessionEntity()

        sessionDao.save(sessionEntity)

        sessionStorage.updateLastActive(sessionEntity.toModel(createServerEntity()))

        sessionDao.find(sessionEntity.id) shouldBe sessionEntity.copy(lastActive = dateTime)
    }

    @Test
    fun getServers_shouldReturnAllServers(): Unit = runBlocking {
        repeat(3) { serverDao.save(createServerEntity()) }

        sessionStorage.getServers().size shouldBe 3
    }

    @Test
    fun getSessionLastUsed_shouldReturnTheSession(): Unit = runBlocking {
        val lastUsedSession = createSessionEntity().copy(
            lastActive = LocalDateTime.now(),
            username = "last used session"
        ).toModel(createServerEntity())

        repeat(2) { sessionDao.save(createSessionEntity()) }
        sessionStorage.save(lastUsedSession)
        repeat(3) { sessionDao.save(createSessionEntity()) }

        sessionStorage.getSessionLastUsed() shouldBe lastUsedSession
    }

    @Test
    fun getSessionLastUsed_shouldReturnNull_whenNoSession(): Unit = runBlocking {
        sessionStorage.getSessionLastUsed() shouldBe null
    }

    @Test
    fun save_shouldSaveSession(): Unit = runBlocking {
        val session = createSessionEntity().toModel(createServerEntity())

        sessionStorage.save(session)

        sessionDao.find(session.id) shouldBe session.toEntity()
        serverDao.find(session.server.id) shouldBe session.server.toEntity()
    }

    @Test
    fun getSessionsWithServerId_shouldReturnSessions(): Unit = runBlocking {
        val server = createServerEntity().toModel()
        val sessions = mutableListOf<Session>()
        repeat(5) { sessions.add(createSessionEntity().toModel(server.toEntity())) }

        repeat(2) { sessionDao.save(createSessionEntity()) }
        sessions.forEach { sessionStorage.save(it) }
        repeat(3) { sessionDao.save(createSessionEntity()) }

        sessionStorage.getSessionsWithServerId(server.id) shouldBe sessions
    }

    @Test
    fun deleteServer(): Unit = runBlocking {
        val session = createSessionEntity().toModel(createServerEntity())

        sessionStorage.save(session)

        sessionStorage.delete(session.server)

        sessionDao.getAll().size shouldBe 1
        serverDao.getAll().size shouldBe 0
    }

    @Test
    fun deleteSession(): Unit = runBlocking {
        val session = createSessionEntity().toModel(createServerEntity())

        sessionStorage.save(session)

        sessionStorage.delete(session)

        sessionDao.getAll().size shouldBe 0
        serverDao.getAll().size shouldBe 1
    }

    private fun createSessionEntity() =
        SessionEntity(
            id = UUID.randomUUID(),
            serverId = UUID.randomUUID(),
            lastActive = LocalDateTime.of(1950, 1, 1, 0, 0),
            profileImageUrl = "placeholder-image-url",
            username = "placeholder-username",
            token = "placeholder-token"
        )

    private fun createServerEntity() =
        ServerEntity(
            id = UUID.randomUUID(),
            hostname = "placeholder-hostname",
            name = "placeholder-name"
        )

    private fun Server.toEntity(): ServerEntity =
        ServerEntity(
            id = id,
            hostname = hostname,
            name = name
        )

    private fun ServerEntity.toModel() =
        Server(
            id = id,
            hostname = hostname,
            name = name
        )


    private fun Session.toEntity() =
        SessionEntity(
            id = id,
            serverId = server.id,
            lastActive = lastActive,
            profileImageUrl = profileImageUrl,
            username = username,
            token = token
        )

    private fun SessionEntity.toModel(serverEntity: ServerEntity) =
        Session(
            id = this.id,
            server = serverEntity.toModel(),
            lastActive = lastActive,
            username = this.username,
            profileImageUrl = profileImageUrl,
            token = this.token
        )
}