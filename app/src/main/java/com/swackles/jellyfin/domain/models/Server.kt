package com.swackles.jellyfin.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "servers")
data class Server(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var host: String = "",
    val lastActive: LocalDateTime = LocalDateTime.now(),
    val username: String = "",
    val password: String = ""
)