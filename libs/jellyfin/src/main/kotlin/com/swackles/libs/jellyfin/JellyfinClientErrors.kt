package com.swackles.libs.jellyfin

sealed class JellyfinClientErrors(message: String, cause: Throwable?): Throwable(message, cause) {
    /**
     * Thrown when the hostname provided is invalid or unreachable.
     */
    class InvalidHostnameError(message: String, cause: Throwable? = null):
        JellyfinClientErrors(message, cause)

    /**
     * Thrown when the Jellyfin server version is unsupported.
     */
    class UnsupportedVersion(
        val serverVersion: String?,
        val supportedVersion: String
    ): JellyfinClientErrors("Client supports version \"$supportedVersion\", but server is ${serverVersion}", null)

    /**
     * Thrown either when authentication fails or has expired.
     */
    class UnauthorizedError(cause: Throwable? = null): JellyfinClientErrors("Unauthorized user", cause)

    class BadDataFormatError(message: String, cause: Throwable? = null):
            JellyfinClientErrors(message, cause)
}