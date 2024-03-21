package com.swackles.jellyfin.data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
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
