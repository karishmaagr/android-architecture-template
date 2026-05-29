package com.example.arch.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")         val id: String,
    @ColumnInfo(name = "title")      val title: String,
    @ColumnInfo(name = "body")       val body: String,
    @ColumnInfo(name = "user_id")    val userId: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)
