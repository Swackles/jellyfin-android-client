package com.swackles.jellyfin.data.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import org.jellyfin.sdk.model.UUID

private object Fields {
    const val ID = "id"
    const val HOSTNAME = "hostname"
    const val USERNAME = "username"
    const val TOKEN = "token"
}

private const val TABLE_NAME = "sessions"

@Entity(TABLE_NAME)
data class SessionEntity(
    @PrimaryKey
    @ColumnInfo(Fields.ID)
    val id: UUID,
    @ColumnInfo(Fields.HOSTNAME)
    val hostname: String,
    @ColumnInfo(Fields.USERNAME)
    val username: String,
    @ColumnInfo(Fields.TOKEN)
    val token: String
)

@Dao
interface SessionDao {
    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun getAll(): List<SessionEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: SessionEntity)
}