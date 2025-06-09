package com.swackles.jellyfin.data.jellyfin

import com.swackles.jellyfin.data.jellyfin.models.JellyfinUser

interface JellyfinClient {
    val libraryService: LibraryService
    val mediaService: MediaService

    /**
     * Logs out the current user from the Jellyfin server.
     *
     * @return A boolean indicating whether the logout was successful.
     */
    suspend fun logout(): Boolean

    /**
     * Returns the access token for the current user.
     */
    suspend fun getJellyfinUser(): JellyfinUser

    /**
     * Returns the ApiClient instance headers. This gets passed to exoplayer so it can stream media from the server.
     */
    fun getHeaders(): HashMap<String, String>
}