package com.swackles.jellyfin.data.room.user

import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.data.room.models.UserAndServer
import kotlinx.coroutines.flow.flow

class UserRepositoryPreview : UserRepository {
    override suspend fun insertOrUpdate(newUser: User) = User.preview()

    override suspend fun getLastActiveUserAndServer() = null

    override suspend fun getUserAndServer(userId: Long) = null

    override fun getAllUsersForServer(serverId: Long) = flow { emit(emptyList<User>()) }
}