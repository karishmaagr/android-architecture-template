package com.example.arch.feature.home.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostDto(
    @Json(name = "id")         val id: String,
    @Json(name = "title")      val title: String,
    @Json(name = "body")       val body: String,
    @Json(name = "user_id")    val userId: String,
    @Json(name = "created_at") val createdAt: Long,
)
