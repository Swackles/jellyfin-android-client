package com.swackles.jellyfin.domain.converters

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.TimeZone


object LocalDateTimeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        if (value == null) return value

        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(value),
            TimeZone.getDefault().toZoneId()
        )
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        if (date == null) return date

        return date.toEpochSecond(ZoneId.systemDefault().rules.getOffset(Instant.now()))
    }
}
