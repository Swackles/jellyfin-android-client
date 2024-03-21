package com.swackles.jellyfin.data.repository

import com.swackles.jellyfin.data.models.User
import kotlinx.coroutines.flow.flow

class UserRepositoryPreview : UserRepository {
    override suspend fun insertOrUpdate(newUser: User) = 1L

    override suspend fun getLastActiveUserAndServer() = null

    override fun getAllUsersForServer(serverId: Int) = flow { emit(null) }
}