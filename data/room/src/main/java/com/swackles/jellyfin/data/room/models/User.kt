package com.swackles.jellyfin.data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val externalId: UUID,
    val serverId: Long = 0,
    val lastActive: LocalDateTime = LocalDateTime.now(),
    val profileImageUrl: String? = null,
    val username: String,
    val token: String,
    val deviceId: String
) {
    companion object {
        fun preview(): User {
            return User(
                id = 0,
                externalId = UUID.randomUUID(),
                username = "Test",
                token = "Token",
                deviceId = UUID.randomUUID().toString()
            )
        }
    }
}
