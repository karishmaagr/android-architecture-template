package com.example.arch.feature.auth.data.mapper

import com.example.arch.core.data.mapper.DtoMapper
import com.example.arch.feature.auth.data.remote.model.LoginResponseDto
import com.example.arch.feature.auth.data.remote.model.UserDto
import com.example.arch.feature.auth.domain.model.User

object AuthMapper : DtoMapper<LoginResponseDto, User> {
    override fun LoginResponseDto.toDomain(): User = user.toDomain()
}

fun UserDto.toDomain(): User = User(
    id        = id,
    email     = email,
    name      = name,
    avatarUrl = avatarUrl,
    createdAt = createdAt,
)
