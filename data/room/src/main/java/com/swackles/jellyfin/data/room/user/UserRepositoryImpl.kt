package com.swackles.jellyfin.data.room.user

import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.data.room.models.UserAndServer

internal class UserRepositoryImpl(private val userDao: UserDao) :
    UserRepository {
    override suspend fun get(userId: Long) = userDao.get(userId)

    override suspend fun insertOrUpdate(newUser: User): User {
        val savedUserId = userDao.insertOrUpdate(newUser.copy(
            id = userDao.getByExternalIdAndServerId(newUser.externalId, newUser.serverId)?.id ?: 0
        ))

        return newUser.copy(id = savedUserId)
    }

    override suspend fun getLastActiveUserAndServer(serverId: Long?): UserAndServer? {
        if (serverId == null) {
            return userDao.getLastActiveUserAndServer()
        } else {
            return userDao.getLastActiveUserAndServer(serverId)
        }
    }

    override suspend fun getUserAndServer(userId: Long) = userDao.getUserAndServer(userId)

    override suspend fun getAllForServer(serverId: Long) = userDao.getAllForServer(serverId)

    override fun getAllUsersForServer(serverId: Long) = userDao.getAllUsersForServer(serverId)

    override suspend fun delete(user: User) = userDao.delete(user)
}