package com.swackles.jellyfin.data.room.user

import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.data.room.models.UserAndServer
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun get(userId: Long): User?

    suspend fun insertOrUpdate(newUser: User): User

    suspend fun getLastActiveUserAndServer(serverId: Long?): UserAndServer?

    suspend fun getUserAndServer(userId: Long): UserAndServer?

    suspend fun getAllForServer(serverId: Long): List<User>

    fun getAllUsersForServer(serverId: Long): Flow<List<User>>

    suspend fun delete(user: User)
}
