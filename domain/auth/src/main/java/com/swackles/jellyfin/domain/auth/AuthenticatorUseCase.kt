package com.swackles.jellyfin.domain.auth

import com.swackles.jellyfin.data.jellyfin.enums.JellyfinResponses
import com.swackles.jellyfin.data.jellyfin.repository.JellyfinRepository
import com.swackles.jellyfin.data.room.models.Server
import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.data.room.models.UserAndServer
import com.swackles.jellyfin.data.room.server.ServerRepository
import com.swackles.jellyfin.data.room.user.UserRepository
import com.swackles.jellyfin.domain.auth.models.AuthenticatorResponse
import io.ktor.http.URLParserException
import org.jellyfin.sdk.api.client.exception.SecureConnectionException
import java.time.LocalDateTime
import javax.inject.Inject

open class AuthenticatorUseCase @Inject constructor(
    private val repository: JellyfinRepository,
    private val serverRepository: ServerRepository,
    private val userRepository: UserRepository
    ) {
    open fun getAuthenticatedUser(): User? {
        return authenticatedUser
    }

    suspend fun loginLastUsedUser(): AuthenticatorResponse {
        val serverUser = userRepository.getLastActiveUserAndServer()
            ?: return AuthenticatorResponse.NO_USER

        println(serverUser)
        return login(serverUser)
    }

    suspend fun login(user: UserAndServer): AuthenticatorResponse {
        try {
            val res = repository.login(
                user.server.host,
                user.username,
                user.password
            )

            if (res.response == JellyfinResponses.SUCCESSFUL) {
                val serverId = serverRepository.insertOrUpdate(user.server.copy(
                    name = res.user?.serverName
                ))

                val copyUser = user.toUser().copy(
                    serverId = serverId,
                    lastActive = LocalDateTime.now(),
                    profileImageUrl = res.user?.profileImageUrl
                )

                userRepository.insertOrUpdate(copyUser)

                authenticatedUser = copyUser
            }

            println("Login: ${res.response}")
            return when(res.response) {
                JellyfinResponses.SUCCESSFUL -> AuthenticatorResponse.SUCCESS
                JellyfinResponses.UNAUTHORIZED_RESPONSE -> AuthenticatorResponse.INVALID_CREDENTIALS
                else -> AuthenticatorResponse.UNKNOWN_ERROR
            }
        } catch (ex: URLParserException) {
            return AuthenticatorResponse.INVALID_URL
        } catch (ex: SecureConnectionException) {
            return AuthenticatorResponse.INVALID_URL
        }
    }

    suspend fun login(host: String, username: String, password: String): AuthenticatorResponse {
        return login(
            UserAndServer(
                username = username,
                password = password,
                server = Server(host = host)
            )
        )
    }

    suspend fun login(username: String, password: String): AuthenticatorResponse {
        val server = serverRepository.getServer(authenticatedUser!!.serverId)
            ?: return AuthenticatorResponse.UNKNOWN_ERROR

        return login(
            UserAndServer(
                username = username,
                password = password,
                server = server
            )
        )
    }

    suspend fun login(id: Long): AuthenticatorResponse {
        return login(userRepository.getUserAndServer(id) ?: return AuthenticatorResponse.NO_USER)
    }

    companion object {
        private var authenticatedUser: User? = null
    }
}