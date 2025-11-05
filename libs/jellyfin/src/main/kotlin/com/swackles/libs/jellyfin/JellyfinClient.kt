package com.swackles.libs.jellyfin

interface JellyfinClient {
    val userClient: UserClient

    val libraryClient: LibraryClient

    val mediaClient: MediaClient
}
