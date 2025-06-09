package com.swackles.jellyfin.data.jellyfin.models

sealed interface Credentials {
    class AccessTokenCredentials(
        val accessToken: String,
    ): Credentials

    class UsernamePasswordCredentials(
        val username: String,
        val password: String,
    ): Credentials
}