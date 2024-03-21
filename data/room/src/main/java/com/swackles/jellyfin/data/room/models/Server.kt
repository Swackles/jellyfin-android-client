package com.swackles.jellyfin.data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class Server(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var host: String,
    var name: String? = null
)