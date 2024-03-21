package com.swackles.jellyfin.data.repository

import com.swackles.jellyfin.data.dao.UserDao
import com.swackles.jellyfin.data.models.User

class UserRepositoryImpl(private val userDao: UserDao) :
    UserRepository {
    override suspend fun insertOrUpdate(newUser: User) = userDao.insertOrUpdate(newUser)

    override suspend fun getLastActiveUserAndServer() = userDao.getLastActiveUserAndServer()

    override fun getAllUsersForServer(serverId: Int) = userDao.getAllUsersForServer(serverId)
}