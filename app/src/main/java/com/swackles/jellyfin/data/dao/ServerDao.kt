package com.swackles.jellyfin.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swackles.jellyfin.data.models.Server

@Dao
interface ServerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(server: Server): Long

    @Query("SELECT * FROM servers")
    fun getAllServers(): List<Server>

}