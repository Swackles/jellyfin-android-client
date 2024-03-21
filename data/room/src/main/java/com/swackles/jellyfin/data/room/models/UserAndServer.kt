package com.swackles.jellyfin.data.room.models

import androidx.room.Relation

data class UserAndServer(
    val id: Long = 0,
    val serverId: Long = 0,
    val username: String,
    val password: String,
    @Relation(
        parentColumn = "serverId",
        entityColumn = "id"
    )
    val server: Server
) {
    fun toUser(): User {
        return User(
            id = id,
            serverId = serverId,
            username = username,
            password = password
        )
    }
}
