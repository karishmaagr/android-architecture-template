package com.example.arch.feature.home.domain.repository

import com.example.arch.core.common.result.Result
import com.example.arch.feature.home.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    // Emits cached data immediately, then refreshes from network
    fun getPosts(): Flow<List<Post>>
    suspend fun refreshPosts(): Result<Unit>
}
