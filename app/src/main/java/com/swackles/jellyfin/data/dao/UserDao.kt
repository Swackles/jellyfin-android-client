package com.swackles.jellyfin.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swackles.jellyfin.data.models.User
import com.swackles.jellyfin.data.models.UserAndServer
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: User): Long

    @Query("SELECT * FROM users ORDER BY lastActive asc LIMIT 1")
    suspend fun getLastActiveUserAndServer(): UserAndServer?

    @Query("SELECT * FROM users WHERE serverId = :serverId ORDER BY lastActive asc")
    fun getAllUsersForServer(serverId: Int): Flow<List<User>?>
}