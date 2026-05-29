package com.example.arch.feature.home.data.repository

import com.example.arch.core.common.result.Result
import com.example.arch.core.data.repository.BaseRepository
import com.example.arch.core.database.dao.PostDao
import com.example.arch.feature.home.data.mapper.PostEntityMapper.toDomain
import com.example.arch.feature.home.data.mapper.toEntity
import com.example.arch.feature.home.data.remote.HomeApi
import com.example.arch.feature.home.domain.model.Post
import com.example.arch.core.data.sync.Syncable
import com.example.arch.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val api: HomeApi,
    private val postDao: PostDao,
) : HomeRepository, Syncable, BaseRepository() {

    override suspend fun sync(): Result<Unit> = refreshPosts()

    override fun getPosts(): Flow<List<Post>> =
        postDao.getAllPosts().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshPosts(): Result<Unit> =
        when (val result = safeApiCall { api.getPosts() }) {
            is Result.Success -> {
                val entities = result.data.map { it.toEntity() }
                postDao.deleteAll()
                postDao.insertAll(entities)
                Result.Success(Unit)
            }
            is Result.Error   -> result
            is Result.Loading -> result
        }
}
