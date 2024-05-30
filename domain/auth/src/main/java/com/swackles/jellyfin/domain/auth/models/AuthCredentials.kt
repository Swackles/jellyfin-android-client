package com.swackles.jellyfin.domain.auth.models

/**
 * Represents the credentials used to authenticate with a Jellyfin server
 * @param host The host of the server to authenticate with. When null, the last used server will be used
 * @param username The username to authenticate with
 * @param password The password to authenticate with
 */
data class AuthCredentials(
    val host: String? = null,
    val username: String = "",
    val password: String = ""
) {
    fun isValid() = (host == null || host.isNotBlank()) && username.isNotBlank() && password.isNotBlank()
}
