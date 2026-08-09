package sk.mkdigital.kmpshowcase.contracts.auth

import sk.mkdigital.kmpshowcase.contracts.user.ThemeModeDTO
import kotlinx.serialization.Serializable

@Serializable
data class AuthUserDTO(
    val id: Long,
    val email: String,
    val themeMode: ThemeModeDTO,
    val locale: String,
)

@Serializable
data class AuthResponseDTO(
    val token: String,
    val user: AuthUserDTO,
)
