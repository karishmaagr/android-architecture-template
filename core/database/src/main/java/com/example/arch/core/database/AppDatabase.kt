package com.example.arch.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.arch.core.database.converter.DateConverter
import com.example.arch.core.database.dao.PostDao
import com.example.arch.core.database.entity.PostEntity

@Database(
    entities = [PostEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}
