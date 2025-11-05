package com.swackles.libs.jellyfin

import androidx.core.net.toUri
import org.jellyfin.sdk.model.api.ImageType
import java.util.UUID

data class JellyfinUser(
    override val id: UUID,
    override val baseUrl: String,
    val username: String,
    val token: String,
    val serverName: String
): JellyfinItem {
    fun getProfileImage(): String =
        this.baseUrl.toUri()
            .buildUpon()
            .appendPath("users")
            .appendPath(id.toString())
            .appendPath("images")
            .appendPath(ImageType.PRIMARY.name)
            .appendPath("0")
            .build()
            .toString()
}

interface UserClient {
    suspend fun endSession()

    suspend fun currentUser(): JellyfinUser
}
