package sk.mkdigital.kmpshowcase.contracts.user

import kotlinx.serialization.Serializable
import sk.mkdigital.kmpshowcase.contracts.LanguageDTO
import sk.mkdigital.kmpshowcase.contracts.ThemeModeDTO

/** @property language defaulted, so a key a newer server adds never fails the read */
@Serializable
data class UserResponseDTO(
    val id: Long,
    val email: String,
    val createdAt: Long,
    val themeMode: ThemeModeDTO,
    val language: LanguageDTO = LanguageDTO.DEFAULT,
    val demo: Boolean = false,
)
