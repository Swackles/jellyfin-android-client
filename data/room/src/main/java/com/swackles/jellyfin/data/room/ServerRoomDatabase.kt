package com.swackles.jellyfin.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.swackles.jellyfin.data.room.converters.LocalDateTimeConverter
import com.swackles.jellyfin.data.room.models.Server
import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.data.room.server.ServerDao
import com.swackles.jellyfin.data.room.user.UserDao

@Database(entities = [Server::class, User::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateTimeConverter::class)
abstract class ServerRoomDatabase : RoomDatabase() {

    abstract fun serverDao(): ServerDao
    abstract fun userDao(): UserDao

    companion object {
        /*The value of a volatile variable will never be cached, and all writes and reads will be done to and from the main memory.
        This helps make sure the value of INSTANCE is always up-to-date and the same for all execution threads.
        It means that changes made by one thread to INSTANCE are visible to all other threads immediately.*/
        @Volatile
        private var INSTANCE: ServerRoomDatabase? = null

        fun getInstance(context: Context): ServerRoomDatabase {
            // only one thread of execution at a time can enter this block of code
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ServerRoomDatabase::class.java,
                    "server_database"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}
