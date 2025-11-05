package com.swackles.jellyfin.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.swackles.jellyfin.data.dao.ServerDao
import com.swackles.jellyfin.data.dao.ServerEntity
import com.swackles.jellyfin.data.dao.SessionDao
import com.swackles.jellyfin.data.dao.SessionEntity
import com.swackles.jellyfin.data.room.converters.LocalDateTimeConverter

@Database(
    entities = [
        ServerEntity::class,
        SessionEntity::class
    ],
    version = 1
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class JellyfinDatabase: RoomDatabase() {
    abstract fun sessionDao(): SessionDao

    abstract fun serverDao(): ServerDao
}