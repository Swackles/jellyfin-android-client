package com.swackles.jellyfin.data.jellyfin

import com.swackles.jellyfin.data.jellyfin.models.Credentials
import com.swackles.jellyfin.data.jellyfin.models.JellyfinUser
import com.swackles.jellyfin.data.jellyfin.util.ServerValidator
import com.swackles.jellyfin.data.jellyfin.util.createJellyfin
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.exception.InvalidStatusException
import org.jellyfin.sdk.api.client.extensions.authenticateUserByName
import org.jellyfin.sdk.api.client.extensions.sessionApi
import org.jellyfin.sdk.api.client.extensions.userApi

class JellyfinClientImpl(
    private val jellyfinClient: ApiClient
): JellyfinClient {

    override val libraryService: LibraryService = LibraryServiceImpl(jellyfinClient)
    override val mediaService: MediaService = MediaServiceImpl(jellyfinClient)

    override suspend fun logout(): Boolean =
        try {
            jellyfinClient.sessionApi.reportSessionEnded()
            true
        } catch (e: Exception) {
            false
        }

    override suspend fun getJellyfinUser(): JellyfinUser =
        jellyfinClient.userApi.getCurrentUser().content.let {
            JellyfinUser(
                id = it.id,
                name = it.name!!,
                token = jellyfinClient.accessToken!!,
                serverName = it.serverName,
                primaryImageTag = it.primaryImageTag,
                host = jellyfinClient.baseUrl!!,
                deviceId = jellyfinClient.deviceInfo.id
            )
        }

    override fun getHeaders(): HashMap<String, String> {
        val values = listOf(
            "Token=\"${jellyfinClient.accessToken}\"",
            "Client=\"${jellyfinClient.clientInfo.name}\"",
            "Version=\"${jellyfinClient.clientInfo.version}\"",
            "DeviceId=\"${jellyfinClient.deviceInfo.id}\"",
            "Device=\"${jellyfinClient.deviceInfo.name}\""
        )

        return hashMapOf(Pair("Authorization", "MediaBrowser ${values.joinToString(", ")}"))
    }

    companion object {
        /**
         * Logs in to the Jellyfin server with the provided access token.
         *
         * @param hostname The hostname of the Jellyfin server.
         * @param credentials The credentials to use for login.
         *
         * @throws [com.swackles.libs.jellyfin.JellyfinClientErrors.InvalidHostnameError] hostname is invalid or unreachable.
         * @throws [com.swackles.libs.jellyfin.JellyfinClientErrors.UnsupportedVersion] server version is unsupported.
         * @throws [com.swackles.libs.jellyfin.JellyfinClientErrors.UnauthorizedError] if access to the server is not granted.
         */
        @Throws(JellyfinClientErrors::class)
        suspend fun login(hostname: String, credentials: Credentials): JellyfinClient =
            when(credentials) {
                is Credentials.AccessTokenCredentials -> login(hostname, credentials)
                is Credentials.UsernamePasswordCredentials -> login(hostname, credentials)
            }

        /**
         * Logs in to the Jellyfin server with the provided access token.
         *
         * @param hostname The hostname of the Jellyfin server.
         * @param credentials The credentials to use for login.
         *
         * @throws [com.swackles.libs.jellyfin.JellyfinClientErrors.InvalidHostnameError] hostname is invalid or unreachable.
         * @throws [com.swackles.libs.jellyfin.JellyfinClientErrors.UnsupportedVersion] server version is unsupported.
         * @throws [com.swackles.libs.jellyfin.JellyfinClientErrors.UnauthorizedError] if access to the server is not granted.
         */
        @Throws(JellyfinClientErrors::class)
        suspend fun login(hostname: String, credentials: Credentials.AccessTokenCredentials): JellyfinClient {
            val client = validateHostnameAndVersion(hostname)
                .also { it.update(accessToken = credentials.accessToken) }


            return try {
                client.userApi.getCurrentUser()

                createInstance(client)
            } catch (ex: InvalidStatusException) {
                when (ex.status) {
                    401 -> throw JellyfinClientErrors.UnauthorizedError()
                    else -> throw ex
                }
            } catch (ex: RuntimeException) {
                throw ex
            }
        }

        /**
         * Logs in to the Jellyfin server with the provided access token.
         *
         * @param hostname The hostname of the Jellyfin server.
         * @param credentials The credentials to use for login.
         *
         * @throws [com.swackles.libs.jellyfin.JellyfinClientErrors.InvalidHostnameError] hostname is invalid or unreachable.
         * @throws [com.swackles.libs.jellyfin.JellyfinClientErrors.UnsupportedVersion] server version is unsupported.
         * @throws [com.swackles.libs.jellyfin.JellyfinClientErrors.InvalidHostnameError] if access to the server is not granted.
         */
        @Throws(JellyfinClientErrors::class)
        suspend fun login(hostname: String, credentials: Credentials.UsernamePasswordCredentials): JellyfinClient {
            val client = validateHostnameAndVersion(hostname)

            return try {
                val result by client.userApi.authenticateUserByName(credentials.username, credentials.password)
                client.update(accessToken = result.accessToken)

                createInstance(client)
            } catch (err: InvalidStatusException) {
                when (err.status) {
                    401 -> throw JellyfinClientErrors.UnauthorizedError()
                    else -> {
                        throw err
                    }
                }
            }
        }

        /**
         * Checks if the provided hostname is valid. And version is supported.
         *
         * @param hostname The hostname to check.
         * @return The hostname if valid.
         *
         * @throws [com.swackles.libs.jellyfin.JellyfinClientErrors.InvalidHostnameError] hostname is invalid or unreachable.
         * @throws [com.swackles.libs.jellyfin.JellyfinClientErrors.UnsupportedVersion] server version is unsupported.
         */
        @Throws(JellyfinClientErrors.InvalidHostnameError::class, JellyfinClientErrors.UnsupportedVersion::class)
        private suspend fun validateHostnameAndVersion(hostname: String): ApiClient {
            val validHostname = ServerValidator.validateHostname(hostname)
                ?: throw JellyfinClientErrors.InvalidHostnameError()

            ServerValidator.validateVersion(validHostname)

            return createJellyfin().createApi(hostname)
        }

        private fun createInstance(client: ApiClient): JellyfinClient =
            JellyfinClientImpl(jellyfinClient = client)
    }
}