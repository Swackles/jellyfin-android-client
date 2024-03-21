package com.swackles.jellyfin.data.useCase

import com.swackles.jellyfin.data.enums.JellyfinResponses
import com.swackles.jellyfin.data.models.AuthenticatorResponse
import com.swackles.jellyfin.data.repository.JellyfinRepository
import com.swackles.jellyfin.data.room.server.ServerRepository
import com.swackles.jellyfin.data.room.user.UserRepository
import io.ktor.http.URLParserException
import org.jellyfin.sdk.api.client.exception.SecureConnectionException
import java.time.LocalDateTime
import javax.inject.Inject

class AuthenticatorUseCase @Inject constructor(
    private val repository: JellyfinRepository,
    private val serverRepository: ServerRepository,
    private val userRepository: UserRepository
    ) {
    suspend fun loginLastUsedUser(): AuthenticatorResponse {
        val serverUser = userRepository.getLastActiveUserAndServer()
            ?: return AuthenticatorResponse.NO_USER

        return login(serverUser)
    }

    suspend fun login(user: com.swackles.jellyfin.data.room.models.UserAndServer): AuthenticatorResponse {
        val res = repository.login(
            user.server.host,
            user.username,
            user.password
        )

        if (res.response == JellyfinResponses.SUCCESSFUL) {
            val serverId = serverRepository.insertOrUpdate(user.server.copy(
                name = res.user?.serverName
            ))

            userRepository.insertOrUpdate(
                user.toUser().copy(
                    serverId = serverId,
                    lastActive = LocalDateTime.now(),
                    profileImageUrl = res.user?.profileImageUrl
                )
            )
        }

        return when(res.response) {
            JellyfinResponses.SUCCESSFUL -> AuthenticatorResponse.SUCCESS
            JellyfinResponses.UNAUTHORIZED_RESPONSE -> AuthenticatorResponse.INVALID_CREDENTIALS
            else -> AuthenticatorResponse.UNKNOWN_ERROR
        }
    }

    suspend fun login(host: String, username: String, password: String): AuthenticatorResponse {
        return try {
            login(
                com.swackles.jellyfin.data.room.models.UserAndServer(
                    username = username,
                    password = password,
                    server = com.swackles.jellyfin.data.room.models.Server(host = host)
                )
            )
        } catch (ex: URLParserException) {
            AuthenticatorResponse.INVALID_URL
        } catch (ex: SecureConnectionException) {
            AuthenticatorResponse.INVALID_URL
        }
    }
}