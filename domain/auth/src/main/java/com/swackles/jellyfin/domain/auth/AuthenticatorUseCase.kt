package com.swackles.jellyfin.domain.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.data.jellyfin.enums.JellyfinResponses
import com.swackles.jellyfin.data.jellyfin.models.JellyfinAuthResponse
import com.swackles.jellyfin.data.jellyfin.models.JellyfinUser
import com.swackles.jellyfin.data.jellyfin.repository.JellyfinRepository
import com.swackles.jellyfin.data.room.models.Server
import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.data.room.models.UserAndServer
import com.swackles.jellyfin.data.room.server.ServerRepository
import com.swackles.jellyfin.data.room.user.UserRepository
import com.swackles.jellyfin.domain.auth.models.AuthCredentials
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
    open val authenticatedUser: LiveData<User?> = AuthenticatorUseCase.authenticatedUser

    suspend fun loginLastUsedUser(serverId: Long? = null): AuthenticatorResponse {
        val serverUser = userRepository.getLastActiveUserAndServer(serverId)
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

    suspend fun login(credentials: AuthCredentials): AuthenticatorResponse {
        val host = credentials.host
            ?: serverRepository.getServer(authenticatedUser.value!!.serverId)?.host
            ?: return AuthenticatorResponse.UNKNOWN_ERROR

        return try {
            handleResponse(repository.login(host, credentials.username, credentials.password))
        } catch (ex: URLParserException) {
            AuthenticatorResponse.INVALID_URL
        } catch (ex: SecureConnectionException) {
            AuthenticatorResponse.INVALID_URL
        }
    }

    suspend fun login(id: Long): AuthenticatorResponse {
        return login(userRepository.getUserAndServer(id) ?: return AuthenticatorResponse.NO_USER)
    }

    suspend fun logoutUser(): AuthenticatorResponse {
        authenticatedUser.value?.let { authUser ->
            userRepository.delete(authUser)
            val users = userRepository.getAllForServer(authUser.serverId)
                .sortedByDescending { it.lastActive }
            repository.logout()

            try {
                return login(users.first().id)
            } catch (ex: NoSuchElementException) {
                return AuthenticatorResponse.LOGOUT
            }
        }

        return AuthenticatorResponse.NO_USER
    }

    suspend fun logoutServer(): AuthenticatorResponse {
        authenticatedUser.value?.let { authUser ->
            userRepository.getAllForServer(authUser.serverId).forEach { user ->
                repository.logout()
                userRepository.delete(user)
            }

            return AuthenticatorResponse.LOGOUT
        }

        return AuthenticatorResponse.NO_USER
    }

    private suspend fun handleResponse(res: JellyfinAuthResponse): AuthenticatorResponse {
        return when(res.response) {
            JellyfinResponses.SUCCESSFUL -> saveUser(res.user!!)
            JellyfinResponses.UNAUTHORIZED_RESPONSE -> AuthenticatorResponse.INVALID_CREDENTIALS
            else -> AuthenticatorResponse.UNKNOWN_ERROR
        }
    }

    private suspend fun saveUser(jellyfinUser: JellyfinUser): AuthenticatorResponse {
        val serverId = serverRepository.insertOrUpdate(
            Server(
                name = jellyfinUser.serverName,
                host = jellyfinUser.host
            )
        )

        AuthenticatorUseCase.authenticatedUser.postValue(userRepository.insertOrUpdate(User(
            externalId = jellyfinUser.id,
            serverId = serverId,
            profileImageUrl = jellyfinUser.getProfileImageUrl(),
            username = jellyfinUser.name,
            token = jellyfinUser.token,
            deviceId = jellyfinUser.deviceId,
            lastActive = LocalDateTime.now()
        )))

        return AuthenticatorResponse.SUCCESS
    }

    companion object {
        private var authenticatedUser = MutableLiveData<User?>(null)
    }
}