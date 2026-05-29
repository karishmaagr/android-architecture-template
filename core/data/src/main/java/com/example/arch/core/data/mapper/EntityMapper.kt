package com.example.arch.core.data.mapper

interface EntityMapper<Entity, DomainModel> {
    fun Entity.toDomain(): DomainModel
    fun DomainModel.toEntity(): Entity
}

interface DtoMapper<Dto, DomainModel> {
    fun Dto.toDomain(): DomainModel
}
