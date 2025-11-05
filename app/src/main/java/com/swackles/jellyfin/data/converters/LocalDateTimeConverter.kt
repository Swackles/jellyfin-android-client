package com.swackles.jellyfin.data.room.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime


internal object LocalDateTimeConverter {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? =
        if (value == null) value else LocalDateTime.parse(value)

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? =
        date?.toString()
}
