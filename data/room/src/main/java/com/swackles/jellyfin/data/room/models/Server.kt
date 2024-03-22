package com.swackles.jellyfin.data.room.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class Server(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val host: String,
    val name: String? = null
)