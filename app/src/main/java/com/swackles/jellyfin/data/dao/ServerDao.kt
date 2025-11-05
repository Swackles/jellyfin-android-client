package com.swackles.jellyfin.data.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.util.UUID

private object ServerFields {
    const val ID = "id"
    const val HOSTNAME = "hostname"
    const val NAME = "name"
}

private const val TABLE_NAME = "servers"

@Entity(tableName = TABLE_NAME)
data class ServerEntity(
    @PrimaryKey
    @ColumnInfo(ServerFields.ID)
    val id: UUID,
    @ColumnInfo(ServerFields.HOSTNAME)
    val hostname: String,
    @ColumnInfo(ServerFields.NAME)
    val name: String
)

@Dao
interface ServerDao {
    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun getAll(): List<ServerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(server: ServerEntity): Long

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    suspend fun find(id: UUID): ServerEntity?

    @Delete
    suspend fun delete(entity: ServerEntity)
}