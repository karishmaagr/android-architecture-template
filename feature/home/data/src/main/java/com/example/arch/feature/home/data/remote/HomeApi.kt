package com.example.arch.feature.home.data.remote

import com.example.arch.core.network.model.ApiResponse
import com.example.arch.feature.home.data.remote.model.PostDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApi {
    @GET("posts")
    suspend fun getPosts(
        @Query("page")     page: Int = 1,
        @Query("per_page") perPage: Int = 20,
    ): Response<ApiResponse<List<PostDto>>>

    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: String): Response<ApiResponse<PostDto>>
}
