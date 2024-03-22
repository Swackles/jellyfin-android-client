package com.swackles.jellyfin.data.room.user

import com.swackles.jellyfin.data.room.models.User

internal class UserRepositoryImpl(private val userDao: UserDao) :
    UserRepository {
    override suspend fun insertOrUpdate(newUser: User) = userDao.insertOrUpdate(newUser)

    override suspend fun getLastActiveUserAndServer() = userDao.getLastActiveUserAndServer()

    override suspend fun getUserAndServer(userId: Long) = userDao.getUserAndServer(userId)

    override fun getAllUsersForServer(serverId: Long) = userDao.getAllUsersForServer(serverId)
}