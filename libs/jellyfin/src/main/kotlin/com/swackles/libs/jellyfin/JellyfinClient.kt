package com.swackles.libs.jellyfin

data class JellyfinUser(
    val username: String,
    val token: String
)

interface JellyfinClient {
    val jellyfinUser: JellyfinUser

    val libraryClient: LibraryClient
}
