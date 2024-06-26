package com.swackles.jellyfin.data.room.server

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swackles.jellyfin.data.room.models.Server
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(server: Server): Long

    @Query("SELECT * FROM servers WHERE id = :id")
    suspend fun getServer(id: Long): Server?

    @Query("SELECT * FROM servers WHERE host = :host")
    suspend fun getServerByHost(host: String): Server?

    @Query("SELECT * FROM servers")
    fun getAllServers(): Flow<List<Server>>

}