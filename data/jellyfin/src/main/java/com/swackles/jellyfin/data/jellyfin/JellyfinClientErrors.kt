package com.swackles.jellyfin.data.jellyfin

sealed class JellyfinClientErrors: Throwable() {
    /**
     * Thrown when the hostname provided is invalid or unreachable.
     */
    class InvalidHostnameError: JellyfinClientErrors()

    /**
     * Thrown when the Jellyfin server version is unsupported.
     */
    class UnsupportedVersion(
        val serverVersion: String?,
        val supportedVersion: String
    ): JellyfinClientErrors()

    /**
     * Thrown either when authentication fails or has expired.
     */
    class UnauthorizedError: JellyfinClientErrors()
}