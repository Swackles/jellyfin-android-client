package com.swackles.jellyfin.data.repository

import com.swackles.jellyfin.data.models.User
import com.swackles.jellyfin.data.models.UserAndServer
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insertOrUpdate(newUser: User): Long

    suspend fun getLastActiveUserAndServer(): UserAndServer?

    fun getAllUsersForServer(serverId: Int): Flow<List<User>?>
}
