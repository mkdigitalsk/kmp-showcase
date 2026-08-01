package com.mk.kmpshowcase.contracts.auth

import com.mk.kmpshowcase.contracts.user.ThemeModeDTO
import kotlinx.serialization.Serializable

@Serializable
data class AuthUserDTO(
    val id: Long,
    val email: String,
    val name: String,
    val themeMode: ThemeModeDTO,
    val locale: String,
)

@Serializable
data class AuthResponseDTO(
    val token: String,
    val user: AuthUserDTO,
)
