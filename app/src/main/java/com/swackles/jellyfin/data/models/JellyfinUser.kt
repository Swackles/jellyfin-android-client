package com.swackles.jellyfin.data.models

import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.AuthenticationResult
import org.jellyfin.sdk.model.api.ImageType

data class JellyfinUser(
    val id: UUID,
    val name: String?,
    val serverName: String?,
    val profileImageUrl: String?,
)

fun AuthenticationResult.toJellyfinUser(baseUrl: String): JellyfinUser {

    return JellyfinUser(
        id = user!!.id,
        name = user?.name,
        serverName = user?.serverName,
        profileImageUrl =if (user?.primaryImageTag!!.isNotBlank())
            "$baseUrl/Users/${user!!.id}/images/${ImageType.PRIMARY.name}?tag=${user!!.primaryImageTag}"
        else null
    )
}
