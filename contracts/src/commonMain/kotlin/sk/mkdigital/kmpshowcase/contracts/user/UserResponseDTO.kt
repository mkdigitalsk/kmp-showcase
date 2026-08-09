package sk.mkdigital.kmpshowcase.contracts.user

import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDTO(
    val id: Long,
    val email: String,
    val createdAt: Long,
    val themeMode: ThemeModeDTO,
    val locale: String,
)
