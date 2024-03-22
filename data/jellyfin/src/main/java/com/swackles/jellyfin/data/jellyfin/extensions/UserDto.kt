package com.swackles.jellyfin.data.jellyfin.extensions

import com.swackles.jellyfin.data.jellyfin.models.JellyfinUser
import org.jellyfin.sdk.model.api.UserDto

fun UserDto.toJellyfinUser(baseUrl: String, token: String, deviceId: String): JellyfinUser {
    return JellyfinUser(
        id,
        name!!,
        token,
        serverName,
        primaryImageTag,
        host = baseUrl,
        deviceId
    )
}