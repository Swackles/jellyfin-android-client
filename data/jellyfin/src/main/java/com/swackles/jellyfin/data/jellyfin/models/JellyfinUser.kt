package com.swackles.jellyfin.data.jellyfin.models

import org.jellyfin.sdk.model.UUID

data class JellyfinUser(
    val id: UUID,
    val name: String?,
    val serverName: String?,
    val profileImageUrl: String?,
)
