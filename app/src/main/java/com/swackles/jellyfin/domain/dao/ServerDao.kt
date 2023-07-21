package com.swackles.jellyfin.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.swackles.jellyfin.domain.models.Server
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
    @Insert
    suspend fun addServer(server: Server)

    @Query("SELECT * FROM servers")
    fun getAllServers(): List<Server>

    @Query("SELECT * FROM servers ORDER BY lastActive asc LIMIT 1")
    fun getLastActiveServer(): Flow<Server?>
}