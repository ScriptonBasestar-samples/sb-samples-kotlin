package me.archmagece.dto

import java.util.UUID

data class LongIdResponseDto(
    val id: Long,
)

data class UUIDResponseDto(
    val id: UUID,
)

data class StringResponseDto(
    val id: String,
)
