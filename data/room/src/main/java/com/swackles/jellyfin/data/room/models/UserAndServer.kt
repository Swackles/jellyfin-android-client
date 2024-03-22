package com.swackles.jellyfin.data.room.models

import androidx.room.Relation
import java.util.UUID

data class UserAndServer(
    val id: Long = 0,
    val externalId: UUID,
    val serverId: Long = 0,
    val username: String,
    val token: String,
    val deviceId: String,
    @Relation(
        parentColumn = "serverId",
        entityColumn = "id"
    )
    val server: Server
)
