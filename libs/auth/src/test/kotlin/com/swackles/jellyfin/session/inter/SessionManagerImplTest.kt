package com.swackles.jellyfin.session.inter

import android.content.Context
import android.util.Log
import com.swackles.jellyfin.session.AuthState
import com.swackles.jellyfin.session.LoginCredentials
import com.swackles.jellyfin.session.Server
import com.swackles.jellyfin.session.Session
import com.swackles.jellyfin.session.SessionManager
import com.swackles.jellyfin.session.SessionStorage
import com.swackles.jellyfin.session.inter.di.JellyfinProviderFactory
import com.swackles.libs.jellyfin.JellyfinCredentials
import com.swackles.libs.jellyfin.JellyfinUser
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test

class SessionManagerImplTest {
    private lateinit var sessionManager: SessionManager
    private lateinit var sessionStorageMock: SessionStorage

    @BeforeEach
    fun setup() {
        sessionStorageMock = mockk<SessionStorage>()
        mockkObject(JellyfinProviderFactory)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        sessionManager = SessionManagerImpl(
            context = mockk<Context>(),
            sessionStorage = sessionStorageMock
        )
    }

    @Test
    fun `initialize should log in last used session`(): Unit = runBlocking {
        val session = createSession()
        mockCurrentUser()

        coEvery { sessionStorageMock.getSessionLastUsed() } returns session
        coEvery { sessionStorageMock.updateLastActive(any()) } returns Unit

        sessionManager.initialize()

        coVerify(exactly = 1) { sessionStorageMock.updateLastActive(session) }
        coVerify(exactly = 1) { JellyfinProviderFactory.login(any(), JellyfinCredentials.Existing(
            hostname = session.server.hostname,
            token = session.token
        )) }

        sessionManager.authState.value shouldBe AuthState.Authenticated(session = session)

    }

    @Test
    fun `initialize do nothing if no session found`(): Unit = runBlocking {

        coEvery { sessionStorageMock.getSessionLastUsed() } returns null

        sessionManager.initialize()

        coVerify(exactly = 0) { sessionStorageMock.updateLastActive(any()) }
        coVerify(exactly = 0) { JellyfinProviderFactory.login(any(), any()) }

        sessionManager.authState.value shouldBe AuthState.Unauthenticated
    }

    @Test
    fun `login using existing session should log in and update last used`(): Unit = runBlocking {
        val credentials = LoginCredentials.ExistingSession(session = createSession())
        mockCurrentUser()

        coEvery { sessionStorageMock.updateLastActive(any()) } returns Unit

        sessionManager.login(credentials)

        coVerify(exactly = 1) { sessionStorageMock.updateLastActive(credentials.session) }
        coVerify(exactly = 1) { JellyfinProviderFactory.login(any(), JellyfinCredentials.Existing(
            hostname = credentials.session.server.hostname,
            token = credentials.session.token
        )) }

        sessionManager.authState.value shouldBe AuthState.Authenticated(session = credentials.session)
    }

    @Test
    fun `login using new user, but existing server session should log in`(): Unit = runBlocking {
        val credentials = LoginCredentials.ExistingServer(
            server = createServer(),
            username = "username",
            password = "password"
        )
        val user = createJellyfinUser()
        mockCurrentUser(user)

        coEvery { sessionStorageMock.save(any()) } returns Unit

        sessionManager.login(credentials)

        val session = Session(
            id = UUID.nameUUIDFromBytes(ByteArray(0)),
            server = credentials.server,
            lastActive = LocalDateTime.MIN,
            profileImageUrl = user.getProfileImage(),
            username = credentials.username,
            token = user.token
        )
        coVerify(exactly = 1) { sessionStorageMock.save(any()) }
        coVerify(exactly = 1) { JellyfinProviderFactory.login(any(), JellyfinCredentials.New(
            hostname = credentials.server.hostname,
            username = credentials.username,
            password = credentials.password
        )) }

        val state = sessionManager.authState.value

        (state is AuthState.Authenticated) shouldBe true
        val sessionState = (state as AuthState.Authenticated).session

        sessionState.server shouldBe session.server
        sessionState.profileImageUrl shouldBe session.profileImageUrl
        sessionState.username shouldBe session.username
        sessionState.token shouldBe session.token
    }

    @Test
    fun `login using new user should log in`(): Unit = runBlocking {
        val credentials = LoginCredentials.NewServer(
            hostname = "hostname",
            username = "username",
            password = "password"
        )
        val user = createJellyfinUser()
        mockCurrentUser(user)

        coEvery { sessionStorageMock.save(any()) } returns Unit

        sessionManager.login(credentials)

        val server = createServer().copy(
            hostname = credentials.hostname,
            name = user.serverName
        )
        val session = createSession(server).copy(
            profileImageUrl = user.getProfileImage(),
            username = credentials.username,
            token = user.token
        )

        coVerify(exactly = 1) { sessionStorageMock.save(any()) }
        coVerify(exactly = 1) { JellyfinProviderFactory.login(any(), JellyfinCredentials.New(
            hostname = credentials.hostname,
            username = credentials.username,
            password = credentials.password
        )) }

        val state = sessionManager.authState.value

        (state is AuthState.Authenticated) shouldBe true
        val sessionState = (state as AuthState.Authenticated).session

        sessionState.server.hostname shouldBe server.hostname
        sessionState.server.name shouldBe server.name

        sessionState.profileImageUrl shouldBe session.profileImageUrl
        sessionState.username shouldBe session.username
        sessionState.token shouldBe session.token
    }

    @Test
    fun `logoutActiveServer should remove server and all sessions when auth state is Authenticated`(): Unit = runBlocking {
        val authState = AuthState.Authenticated(createSession())
        forceAuthState(authState)

        coEvery { sessionStorageMock.getSessionsWithServerId(authState.session.server.id) } returns listOf(
            createSession(),
            authState.session,
            createSession(),
        )
        coEvery { sessionStorageMock.delete(any<Server>()) } returns Unit
        coEvery { sessionStorageMock.delete(any<Session>()) } returns Unit
        coEvery { JellyfinProviderFactory.logOut(any()) } returns Unit

        sessionManager.logoutActiveServer()

        coVerify(exactly = 3) { sessionStorageMock.delete(any<Session>()) }
        coVerify(exactly = 1) { sessionStorageMock.delete(authState.session.server) }
        coVerify { JellyfinProviderFactory.logOut(any()) }
    }

    @Test
    fun `logoutActiveServer should do nothing when auth state is not Authenticated`(): Unit = runBlocking {
        listOf(AuthState.Loading, AuthState.Unauthenticated).forEach {
            forceAuthState(it)

            sessionManager.logoutActiveServer()

            coVerify(exactly = 0) { sessionStorageMock.delete(any<Session>()) }
            coVerify(exactly = 0) { sessionStorageMock.delete(any<Server>()) }
            coVerify(exactly = 0) { JellyfinProviderFactory.logOut(any()) }
        }
    }

    @Test
    fun `logoutActiveSession should remove session when state is authenticated`(): Unit = runBlocking {
        val authState = AuthState.Authenticated(createSession())
        forceAuthState(authState)

        coEvery { sessionStorageMock.getSessionsWithServerId(authState.session.server.id) } returns listOf(
            createSession(),
            createSession(),
        )
        coEvery { sessionStorageMock.delete(any<Server>()) } returns Unit
        coEvery { sessionStorageMock.delete(any<Session>()) } returns Unit
        coEvery { JellyfinProviderFactory.logOut(any()) } returns Unit

        sessionManager.logoutActiveSession()

        coVerify(exactly = 1) { sessionStorageMock.delete(authState.session) }
        coVerify(exactly = 0) { sessionStorageMock.delete(any<Server>()) }
        coVerify { JellyfinProviderFactory.logOut(any()) }
    }

    @Test
    fun `logoutActiveSession should remove session when state is authenticated and server when no sessions are associated with the server`(): Unit = runBlocking {
        val authState = AuthState.Authenticated(createSession())
        forceAuthState(authState)

        coEvery { sessionStorageMock.getSessionsWithServerId(authState.session.server.id) } returns emptyList()
        coEvery { sessionStorageMock.delete(any<Server>()) } returns Unit
        coEvery { sessionStorageMock.delete(any<Session>()) } returns Unit
        coEvery { JellyfinProviderFactory.logOut(any()) } returns Unit

        sessionManager.logoutActiveSession()

        coVerify(exactly = 1) { sessionStorageMock.delete(authState.session) }
        coVerify(exactly = 1) { sessionStorageMock.delete(authState.session.server) }
        coVerify { JellyfinProviderFactory.logOut(any()) }
    }

    @Test
    fun `logoutActiveSession should do nothing when auth state is not Authenticated`(): Unit = runBlocking {
        listOf(AuthState.Loading, AuthState.Unauthenticated).forEach {
            forceAuthState(it)

            sessionManager.logoutActiveSession()

            coVerify(exactly = 0) { sessionStorageMock.delete(any<Session>()) }
            coVerify(exactly = 0) { sessionStorageMock.delete(any<Server>()) }
            coVerify(exactly = 0) { JellyfinProviderFactory.logOut(any()) }
        }
    }

    private fun forceAuthState(authState: AuthState) {
        val field = sessionManager.javaClass.getDeclaredField("_authState")
        field.isAccessible = true
        val mutableStateFlow = field.get(sessionManager) as MutableStateFlow<AuthState>
        mutableStateFlow.value = authState
    }

    private fun mockCurrentUser(user: JellyfinUser = createJellyfinUser()) {
        coEvery { JellyfinProviderFactory.login(any(), any()) } returns mockk {
            every { userClient } returns mockk {
                coEvery { currentUser() } returns user
            }
        }
    }

    private fun createSession(server: Server = createServer()) =
        Session(
            id = UUID.randomUUID(),
            server = server,
            lastActive = LocalDateTime.of(1950, 1, 1, 0, 0),
            profileImageUrl = "placeholder-image-url",
            username = "placeholder-username",
            token = "placeholder-token"
        )

    private fun createServer() =
        Server(
            id = UUID.randomUUID(),
            hostname = "placeholder-hostname",
            name = "placeholder-name"
        )

    private fun createJellyfinUser(block: JellyfinUser.() -> Unit = {}) =
        mockk<JellyfinUser> {
            every { getProfileImage() } returns "profile-image"
            every { token } returns "token"
            every { username } returns "username"
            every { serverName } returns "server-name"

            block()
        }
}