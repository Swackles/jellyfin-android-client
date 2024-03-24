package com.swackles.jellyfin.data.room.user

import com.swackles.jellyfin.data.room.models.User
import kotlinx.coroutines.flow.flow

class UserRepositoryPreview : UserRepository {
    override suspend fun get(userId: Long) = User.preview()

    override suspend fun insertOrUpdate(newUser: User) = User.preview()

    override suspend fun getLastActiveUserAndServer() = null

    override suspend fun getUserAndServer(userId: Long) = null

    override fun getAllUsersForServer(serverId: Long) = flow { emit(emptyList<User>()) }

    override suspend fun getAllForServer(serverId: Long) = emptyList<User>()

    override suspend fun delete(user: User) {  }
}