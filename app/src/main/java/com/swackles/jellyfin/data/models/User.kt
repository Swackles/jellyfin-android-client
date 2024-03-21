package com.swackles.jellyfin.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDateTime

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val serverId: Long = 0,
    val lastActive: LocalDateTime = LocalDateTime.now(),
    val profileImageUrl: String? = null,
    val username: String,
    val password: String
)

data class UserAndServer(
    val id: Long = 0,
    val serverId: Long = 0,
    val username: String,
    val password: String,
    @Relation(
        parentColumn = "serverId",
        entityColumn = "id"
    )
    val server: Server,
)

fun UserAndServer.toUser(): User {
    return User(
        id = id,
        serverId = serverId,
        username = username,
        password = password
    )
}