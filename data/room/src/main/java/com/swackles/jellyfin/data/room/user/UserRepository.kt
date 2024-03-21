package com.swackles.jellyfin.data.room.user

import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.data.room.models.UserAndServer
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insertOrUpdate(newUser: User): Long

    suspend fun getLastActiveUserAndServer(): UserAndServer?

    fun getAllUsersForServer(serverId: Int): Flow<List<User>?>
}
