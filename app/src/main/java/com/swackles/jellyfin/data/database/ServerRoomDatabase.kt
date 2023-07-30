package com.swackles.jellyfin.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.swackles.jellyfin.data.converters.LocalDateTimeConverter
import com.swackles.jellyfin.data.models.Server
import com.swackles.jellyfin.data.dao.ServerDao

@Database(entities = [(Server::class)], version = 1, exportSchema = false)
@TypeConverters(LocalDateTimeConverter::class)
abstract class ServerRoomDatabase : RoomDatabase() {

    abstract fun serverDao(): ServerDao

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
