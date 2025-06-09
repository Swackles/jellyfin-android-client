package com.swackles.jellyfin.data.jellyfin.util

import com.swackles.jellyfin.data.jellyfin.JellyfinClientErrors
import org.jellyfin.sdk.api.client.extensions.systemApi

internal class ServerValidator {
    companion object {
        private const val HOSTNAME_SCHEMA = "schema"
        private const val HOSTNAME_BASE_URL = "baseurl"
        private val HOSTNAME_REGEX = Regex("^(?<$HOSTNAME_SCHEMA>http|https|)(:\\/\\/|)(?<$HOSTNAME_BASE_URL>[\\w\\d\\.]+)\$")

        private const val SUPPORTED_VERSION = 10

        internal suspend fun validateHostname(hostname: String): String? {
            val matchResult = HOSTNAME_REGEX.find(hostname)
                ?: throw JellyfinClientErrors.InvalidHostnameError()

            val protocol = matchResult.groups[HOSTNAME_SCHEMA]?.value

            if (protocol.isNullOrBlank()) return isHostnameValid(hostname).let { isValid ->
                if (isValid) hostname else null
            }

            val baseurl = matchResult.groups[HOSTNAME_BASE_URL]?.value
            if (isHostnameValid("https://$baseurl")) return "https://$baseurl"
            if (isHostnameValid("http://$baseurl")) return "http://$baseurl"

            return null
        }

        internal suspend fun validateVersion(hostname: String): Unit =
            createJellyfin()
                .createApi(hostname)
                .systemApi.getPublicSystemInfo().let {
                    val serverVersion = it.content.version

                    SUPPORTED_VERSION == serverVersion?.split(".")?.first()?.toIntOrNull()
                            || throw JellyfinClientErrors.UnsupportedVersion(
                        serverVersion = serverVersion,
                        supportedVersion = SUPPORTED_VERSION.toString())
                }

        private suspend fun isHostnameValid(baseUrl: String): Boolean = try {
            createJellyfin()
                .createApi(baseUrl = baseUrl)
                .systemApi.getPingSystem()
                .status.let { status ->
                    return when (status) {
                        200 -> true
                        else -> false
                    }
                }
        } catch (ex: Exception) {
            false
        }
    }
}