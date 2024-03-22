package com.swackles.jellyfin.data.jellyfin.models

import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.ImageType

data class JellyfinUser(
    val id: UUID,
    val name: String,
    val token: String,
    val serverName: String?,
    val primaryImageTag: String?,
    val host: String,
    val deviceId: String
) {
    fun getProfileImageUrl(): String? = primaryImageTag?.let {
        "$host/Users/$id/images/${ImageType.PRIMARY.name}?tag=$primaryImageTag"
    }
}
