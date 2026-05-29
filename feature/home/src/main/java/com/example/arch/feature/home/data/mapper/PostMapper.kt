package com.example.arch.feature.home.data.mapper

import com.example.arch.core.data.mapper.DtoMapper
import com.example.arch.core.data.mapper.EntityMapper
import com.example.arch.core.database.entity.PostEntity
import com.example.arch.feature.home.data.remote.model.PostDto
import com.example.arch.feature.home.domain.model.Post

object PostDtoMapper : DtoMapper<PostDto, Post> {
    override fun PostDto.toDomain() = Post(
        id        = id,
        title     = title,
        body      = body,
        userId    = userId,
        createdAt = createdAt,
    )
}

object PostEntityMapper : EntityMapper<PostEntity, Post> {
    override fun PostEntity.toDomain() = Post(
        id        = id,
        title     = title,
        body      = body,
        userId    = userId,
        createdAt = createdAt,
    )

    override fun Post.toEntity() = PostEntity(
        id        = id,
        title     = title,
        body      = body,
        userId    = userId,
        createdAt = createdAt,
    )
}

fun PostDto.toEntity() = PostEntity(
    id        = id,
    title     = title,
    body      = body,
    userId    = userId,
    createdAt = createdAt,
)
