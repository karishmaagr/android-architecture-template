package com.example.arch.core.database.converter

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {
    @TypeConverter fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }
    @TypeConverter fun toTimestamp(date: Date?): Long? = date?.time
}
