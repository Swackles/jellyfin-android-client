package com.swackles.libs.jellyfin

import android.content.Context
import com.swackles.libs.jellyfin.inter.LibraryClientImpl
import com.swackles.libs.jellyfin.inter.MediaClientImpl
import com.swackles.libs.jellyfin.inter.UserClientImpl
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.JellyfinOptions
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.exception.InvalidStatusException
import org.jellyfin.sdk.api.client.exception.TimeoutException
import org.jellyfin.sdk.api.client.extensions.authenticateUserByName
import org.jellyfin.sdk.api.client.extensions.systemApi
import org.jellyfin.sdk.api.client.extensions.userApi
import org.jellyfin.sdk.model.ClientInfo
import org.jellyfin.sdk.model.DeviceInfo
import java.util.UUID

internal object Factory {
    fun createJellyfin(context: Context): Jellyfin {
        val options = JellyfinOptions.Builder()
            .apply {
                this.context = context
                clientInfo = ClientInfo(name = "Jellyfin WIP app",  version = "0.0.1-ALPHA")
                deviceInfo = DeviceInfo(id = UUID.randomUUID().toString(), name = "Mobile Device")
            }.build()

        return Jellyfin(options)
    }
}

sealed interface JellyfinCredentials {
    data class New(
        val hostname: String,
        val username: String,
        val password: String
    ): JellyfinCredentials

    data class Existing(
        val hostname: String,
        val token: String
    ): JellyfinCredentials
}

class JellyfinClientImpl(
    private val apiClient: ApiClient
): JellyfinClient {
    override val userClient: UserClient = UserClientImpl(apiClient)
    override val libraryClient: LibraryClient = LibraryClientImpl(apiClient)
    override val mediaClient: MediaClient = MediaClientImpl(apiClient)

    override fun getHeaders(): HashMap<String, String> {
        val values = listOf(
            "Token=\"${apiClient.accessToken}\"",
            "Client=\"${apiClient.clientInfo.name}\"",
            "Version=\"${apiClient.clientInfo.version}\"",
            "DeviceId=\"${apiClient.deviceInfo.id}\"",
            "Device=\"${apiClient.deviceInfo.name}\""
        )

        return hashMapOf(Pair("Authorization", "MediaBrowser ${values.joinToString(", ")}"))
    }

    companion object {
        private const val SUPPORTED_VERSION = "10"

        suspend fun login(context: Context, credentials: JellyfinCredentials): JellyfinClient =
            try {
                when(credentials) {
                    is JellyfinCredentials.New ->
                        login(context, hostname = credentials.hostname, username = credentials.username, password = credentials.password)
                    is JellyfinCredentials.Existing ->
                        login(context, hostname = credentials.hostname, accessToken = credentials.token)
                }
            } catch (ex: InvalidStatusException) {
                when (ex.status) {
                    401 -> throw JellyfinClientErrors.UnauthorizedError()
                    else -> throw ex
                }
            }

        @Throws(JellyfinClientErrors::class)
        private suspend fun login(context: Context, hostname: String, accessToken: String): JellyfinClient {
            val client = createApiClient(context, hostname, accessToken)

            client.userApi.getCurrentUser()

            return JellyfinClientImpl(apiClient = client)
        }

        @Throws(JellyfinClientErrors::class)
        private suspend fun login(context: Context, hostname: String, username: String, password: String): JellyfinClient {
            val client = createApiClient(context, hostname)

            val result by client.userApi.authenticateUserByName(username = username, password = password)
            client.update(accessToken = result.accessToken)

            return JellyfinClientImpl(apiClient = client)
        }

        private suspend fun createApiClient(context: Context, hostname: String, accessToken: String? = null): ApiClient {
            val client = Factory.createJellyfin(context)
                .createApi(baseUrl = hostname, accessToken = accessToken)

            try {
                client.systemApi.getPingSystem().status.let { status ->
                    if (status != 200) throw JellyfinClientErrors.InvalidHostnameError("Hostname \"$hostname\" is not valid")
                }
            } catch (err: TimeoutException) {
                throw JellyfinClientErrors.InvalidHostnameError("Hostname \"$hostname\" is not valid", cause = err)
            }

            client.systemApi.getPublicSystemInfo().content.version.let {
                if (SUPPORTED_VERSION != it?.split(".")?.first())
                    throw JellyfinClientErrors.UnsupportedVersion(it, SUPPORTED_VERSION)
            }

            return client
        }
    }
}