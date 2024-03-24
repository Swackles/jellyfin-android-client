package com.swackles.jellyfin.domain.auth

import com.swackles.jellyfin.data.jellyfin.enums.JellyfinResponses
import com.swackles.jellyfin.data.jellyfin.models.JellyfinAuthResponse
import com.swackles.jellyfin.data.jellyfin.models.JellyfinUser
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

        return login(serverUser)
    }

    suspend fun login(user: UserAndServer): AuthenticatorResponse {
        return try {
            handleResponse(repository.login(user.server.host, user.externalId, user.token, user.deviceId))
        } catch (ex: URLParserException) {
            AuthenticatorResponse.INVALID_URL
        } catch (ex: SecureConnectionException) {
            AuthenticatorResponse.INVALID_URL
        }
    }

    suspend fun login(host: String, username: String, password: String): AuthenticatorResponse {
        return try {
            handleResponse(repository.login(host, username, password))
        } catch (ex: URLParserException) {
            AuthenticatorResponse.INVALID_URL
        } catch (ex: SecureConnectionException) {
            AuthenticatorResponse.INVALID_URL
        }
    }

    suspend fun login(username: String, password: String): AuthenticatorResponse {
        val server = serverRepository.getServer(authenticatedUser!!.serverId)
            ?: return AuthenticatorResponse.UNKNOWN_ERROR

        return login(server.host, username, password)
    }

    suspend fun login(id: Long): AuthenticatorResponse {
        return login(userRepository.getUserAndServer(id) ?: return AuthenticatorResponse.NO_USER)
    }

    private suspend fun handleResponse(res: JellyfinAuthResponse): AuthenticatorResponse {
        return when(res.response) {
            JellyfinResponses.SUCCESSFUL -> saveUser(res.user!!)
            JellyfinResponses.UNAUTHORIZED_RESPONSE -> AuthenticatorResponse.INVALID_CREDENTIALS
            else -> AuthenticatorResponse.UNKNOWN_ERROR
        }
    }

    private suspend fun saveUser(jellyfinUser: JellyfinUser): AuthenticatorResponse {
        println("AuthenticatorUseCase.saveUser()")
        val serverId = serverRepository.insertOrUpdate(
            Server(
                name = jellyfinUser.serverName,
                host = jellyfinUser.host
            )
        )

        val copyUser = User(
            externalId = jellyfinUser.id,
            serverId = serverId,
            profileImageUrl = jellyfinUser.getProfileImageUrl(),
            username = jellyfinUser.name,
            token = jellyfinUser.token,
            deviceId = jellyfinUser.deviceId,
            lastActive = LocalDateTime.now()
        )

        authenticatedUser = userRepository.insertOrUpdate(copyUser)

        return AuthenticatorResponse.SUCCESS
    }

    companion object {
        private var authenticatedUser: User? = null
    }
}