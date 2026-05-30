package com.example.nobelapi.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDateTime

@Serializable
data class UserPrize(
    val userId: Int,
    val prizeId: Int,
    @Contextual val addedAt: LocalDateTime
)