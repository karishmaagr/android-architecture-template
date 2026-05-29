package com.example.arch.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "data")    val data: T?,
    @Json(name = "message") val message: String?,
    @Json(name = "success") val success: Boolean,
    @Json(name = "code")    val code: Int,
)

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "message")  val message: String,
    @Json(name = "code")     val code: Int,
    @Json(name = "errors")   val errors: Map<String, List<String>>? = null,
)

@JsonClass(generateAdapter = true)
data class PaginatedResponse<T>(
    @Json(name = "data")        val data: List<T>,
    @Json(name = "page")        val page: Int,
    @Json(name = "per_page")    val perPage: Int,
    @Json(name = "total")       val total: Int,
    @Json(name = "total_pages") val totalPages: Int,
)
