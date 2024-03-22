package com.swackles.jellyfin.data.jellyfin.extensions

import com.swackles.jellyfin.data.jellyfin.models.JellyfinUser
import org.jellyfin.sdk.model.api.AuthenticationResult
import org.jellyfin.sdk.model.api.ImageType

fun AuthenticationResult.toJellyfinUser(baseUrl: String): JellyfinUser {
    return JellyfinUser(
        id = user!!.id,
        name = user?.name,
        serverName = user?.serverName,
        profileImageUrl =if (user?.primaryImageTag != null)
            "$baseUrl/Users/${user!!.id}/images/${ImageType.PRIMARY.name}?tag=${user!!.primaryImageTag}"
        else null
    )
}
