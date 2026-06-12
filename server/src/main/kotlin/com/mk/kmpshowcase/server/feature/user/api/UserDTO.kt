package com.mk.kmpshowcase.server.feature.user.api

import com.mk.kmpshowcase.server.feature.user.service.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Long,
    val email: String,
    val name: String,
    val createdAt: Long,
)

fun User.toDTO() = UserDTO(
    id = id,
    email = email,
    name = name,
    createdAt = createdAt,
)
