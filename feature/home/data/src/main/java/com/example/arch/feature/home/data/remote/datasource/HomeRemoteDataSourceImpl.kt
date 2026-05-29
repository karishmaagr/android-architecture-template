package com.example.arch.feature.home.data.remote.datasource

import com.example.arch.core.common.result.Result
import com.example.arch.core.data.repository.BaseRepository
import com.example.arch.feature.home.data.remote.HomeApi
import com.example.arch.feature.home.data.remote.model.PostDto
import javax.inject.Inject

interface HomeRemoteDataSource {
    suspend fun getPosts(page: Int, perPage: Int): Result<List<PostDto>>
}

class HomeRemoteDataSourceImpl @Inject constructor(
    private val api: HomeApi,
) : HomeRemoteDataSource, BaseRepository() {

    override suspend fun getPosts(page: Int, perPage: Int): Result<List<PostDto>> =
        safeApiCall { api.getPosts(page, perPage) }
}
