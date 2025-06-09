package com.swackles.jellyfin.domain.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.data.jellyfin.JellyfinClientErrors
import com.swackles.jellyfin.data.jellyfin.JellyfinClientImpl
import com.swackles.jellyfin.data.jellyfin.models.Credentials
import com.swackles.jellyfin.data.room.models.Server
import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.data.room.models.UserAndServer
import com.swackles.jellyfin.data.room.server.ServerRepository
import com.swackles.jellyfin.data.room.user.UserRepository
import com.swackles.jellyfin.domain.auth.models.AuthCredentials
import com.swackles.jellyfin.domain.auth.models.AuthenticatorResponse
import java.time.LocalDateTime
import javax.inject.Inject

open class AuthenticatorUseCase @Inject constructor(
    private val serverRepository: ServerRepository,
    private val userRepository: UserRepository
    ) {
    open val authenticatedUser: LiveData<User?> = AuthenticatorUseCase.authenticatedUser

    /**
     * Logs in the last used user
     * @param serverId The id of the server to log in to. When null, the server will be the last used server
     */
    suspend fun loginLastUsedUser(serverId: Long? = null): AuthenticatorResponse {
        val serverUser = userRepository.getLastActiveUserAndServer(serverId)
            ?: return AuthenticatorResponse.NO_USER

        return login(serverUser)
    }

    /**
     * Logs in a user with existing credentials, used for already authenticated users
     */
    suspend fun login(user: UserAndServer): AuthenticatorResponse =
        performLogin(user.server.host, Credentials.AccessTokenCredentials(user.token))

    /**
     * Logs in a user with the provided credentials, used for new users
     */
    suspend fun login(credentials: AuthCredentials): AuthenticatorResponse {
        // use if host in AuthCredentials is null use the serverId of the current user
        val hosts = generateAcceptableHosts(credentials.host
            ?: serverRepository.getServer(authenticatedUser.value!!.serverId)?.host
            ?: return AuthenticatorResponse.UNKNOWN_ERROR)

        for (host in hosts) {
            return performLogin(host, Credentials.UsernamePasswordCredentials(
                username = credentials.username,
                password = credentials.password
            ))
        }

        // Code should not reach here, so return unknown error
        return AuthenticatorResponse.UNKNOWN_ERROR
    }

    /**
     * Logs in a user with the provided id
     * @param id The id of the user to log in
     */
    suspend fun login(id: Long): AuthenticatorResponse {
        return login(userRepository.getUserAndServer(id) ?: return AuthenticatorResponse.NO_USER)
    }

    suspend fun logoutUser(): AuthenticatorResponse {
        authenticatedUser.value?.let { authUser ->
            userRepository.delete(authUser)
            val users = userRepository.getAllForServer(authUser.serverId)
                .sortedByDescending { it.lastActive }

            AuthContext.getJellyfinClient().logout()
            AuthContext.clearJellyfinClient()

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

                AuthContext.getJellyfinClient().logout()
                AuthContext.clearJellyfinClient()

                userRepository.delete(user)
            }

            return AuthenticatorResponse.LOGOUT
        }

        return AuthenticatorResponse.NO_USER
    }

    private suspend fun performLogin(host: String, credentials: Credentials): AuthenticatorResponse =
        try {
            Log.d("AuthenticatorUseCase", "Attempting to login to $host")

            val client = JellyfinClientImpl.login(host, credentials)
            val jellyfinUser = client.getJellyfinUser()

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

            AuthContext.setJellyfinClient(client)
            AuthenticatorResponse.SUCCESS
        } catch (ex: JellyfinClientErrors) {
            when(ex) {
                is JellyfinClientErrors.InvalidHostnameError -> AuthenticatorResponse.INVALID_URL
                is JellyfinClientErrors.UnauthorizedError -> AuthenticatorResponse.INVALID_CREDENTIALS
                else -> throw ex
            }
        } catch (ex: Exception) {
            Log.e("AuthenticatorUseCase", "Error logging in to $host", ex)
            AuthenticatorResponse.UNKNOWN_ERROR
        }


    private fun generateAcceptableHosts(host: String): List<String> {
        val hosts = mutableListOf<String>()

        // Even if it supports HTTP, we will always try HTTPS first
        if (host.startsWith(URL_RROTOCOL_HTTP)) {
            hosts.add(host.replace(URL_RROTOCOL_HTTP, URL_RROTOCOL_HTTPS))
            hosts.add(host)
        } else if (host.startsWith(URL_RROTOCOL_HTTPS)) {
            hosts.add(host)
            hosts.add(host.replace(URL_RROTOCOL_HTTPS, URL_RROTOCOL_HTTP))
        } else {
            hosts.add(URL_RROTOCOL_HTTPS + host)
            hosts.add(URL_RROTOCOL_HTTP + host)
        }

        return hosts
    }

    companion object {
        private var authenticatedUser = MutableLiveData<User?>(null)

        private val URL_RROTOCOL_HTTP = "http://"
        private val URL_RROTOCOL_HTTPS = "https://"
    }
}