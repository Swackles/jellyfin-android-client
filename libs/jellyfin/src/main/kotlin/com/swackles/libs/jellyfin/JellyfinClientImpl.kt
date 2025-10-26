package com.swackles.libs.jellyfin

import android.content.Context
import kotlinx.coroutines.runBlocking
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

class JellyfinClientImpl(
    private val apiClient: ApiClient
): JellyfinClient {
    override val jellyfinUser: JellyfinUser
        get() = runBlocking {
            JellyfinUser(
                username = apiClient.userApi.getCurrentUser().content.name!!,
                token = apiClient.accessToken!!
            )
        }

    companion object {
        private const val SUPPORTED_VERSION = "10"

        @Throws(JellyfinClientErrors::class)
        suspend fun login(context: Context, hostname: String, accessToken: String): com.swackles.libs.jellyfin.JellyfinClient {
            val client = createApiClient(context, hostname, accessToken)

            try {
                client.userApi.getCurrentUser()

                return JellyfinClientImpl(apiClient = client)
            } catch (ex: InvalidStatusException) {
                when (ex.status) {
                    401 -> throw JellyfinClientErrors.UnauthorizedError()
                    else -> throw ex
                }
            }
        }

        @Throws(JellyfinClientErrors::class)
        suspend fun login(context: Context, hostname: String, username: String, password: String): com.swackles.libs.jellyfin.JellyfinClient {
            val client = createApiClient(context, hostname)

            try {
                val result by client.userApi.authenticateUserByName(username = username, password = password)
                client.update(accessToken = result.accessToken)

                return JellyfinClientImpl(apiClient = client)
            } catch (err: InvalidStatusException) {
                when (err.status) {
                    400, 401 -> throw JellyfinClientErrors.UnauthorizedError(err)
                    else -> {
                        throw err
                    }
                }
            }
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