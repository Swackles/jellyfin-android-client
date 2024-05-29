package com.swackles.jellyfin.data.room.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.data.room.models.UserAndServer
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: User): Long

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun get(userId: Long): User?

    @Query("SELECT * FROM users ORDER BY lastActive desc LIMIT 1")
    suspend fun getLastActiveUserAndServer(): UserAndServer?

    @Query("SELECT * FROM users WHERE serverId = :serverId ORDER BY lastActive desc LIMIT 1")
    suspend fun getLastActiveUserAndServer(serverId: Long): UserAndServer?

    @Query("SELECT * FROM users WHERE externalId = :externalId AND serverId = :serverId LIMIT 1")
    suspend fun getByExternalIdAndServerId(externalId: UUID, serverId: Long): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserAndServer(userId: Long): UserAndServer?

    @Query("SELECT * FROM users WHERE serverId = :serverId ORDER BY username asc")
    suspend fun getAllForServer(serverId: Long): List<User>

    @Query("SELECT * FROM users WHERE serverId = :serverId ORDER BY username asc")
    fun getAllUsersForServer(serverId: Long): Flow<List<User>>

    @Delete
    suspend fun delete(user: User)
}