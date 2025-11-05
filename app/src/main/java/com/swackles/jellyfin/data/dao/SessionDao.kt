package com.swackles.jellyfin.data.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.time.LocalDateTime
import java.util.UUID

private object SessionFields {
    const val ID = "id"
    const val SERVER_ID = "server_id"
    const val LAST_ACTIVE = "last_active"
    const val PROFILE_IMAGE_URL = "profile_image_url"
    const val USERNAME = "username"
    const val TOKEN = "token"
}

private const val TABLE_NAME = "sessions"

@Entity(TABLE_NAME)
data class SessionEntity(
    @PrimaryKey
    @ColumnInfo(SessionFields.ID)
    val id: UUID,
    @ColumnInfo(SessionFields.SERVER_ID)
    val serverId: UUID,
    @ColumnInfo(SessionFields.LAST_ACTIVE)
    val lastActive: LocalDateTime,
    @ColumnInfo(SessionFields.PROFILE_IMAGE_URL)
    val profileImageUrl: String,
    @ColumnInfo(SessionFields.USERNAME)
    val username: String,
    @ColumnInfo(SessionFields.TOKEN)
    val token: String
)

@Dao
interface SessionDao {
    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    suspend fun find(id: UUID): SessionEntity?

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun getAll(): List<SessionEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE ${SessionFields.SERVER_ID} = :serverId")
    suspend fun getAllWithServerId(serverId: UUID): List<SessionEntity>

    @Query("SELECT * FROM $TABLE_NAME ORDER BY ${SessionFields.LAST_ACTIVE} desc LIMIT 1")
    suspend fun getLastUsedSession(): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: SessionEntity)

    @Delete
    suspend fun delete(entity: SessionEntity)

    @Delete
    suspend fun delete(entity: List<SessionEntity>)
}