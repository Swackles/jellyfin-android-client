package com.swackles.jellyfin.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.swackles.jellyfin.data.dao.SessionDao
import com.swackles.jellyfin.data.dao.SessionEntity

@Database(
    entities = [
        SessionEntity::class
    ],
    version = 1
)
abstract class JellyfinDatabase: RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}