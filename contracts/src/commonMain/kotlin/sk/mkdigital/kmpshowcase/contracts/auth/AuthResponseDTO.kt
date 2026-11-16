package sk.mkdigital.kmpshowcase.contracts.auth

import sk.mkdigital.kmpshowcase.contracts.LanguageDTO
import sk.mkdigital.kmpshowcase.contracts.ThemeModeDTO
import kotlinx.serialization.Serializable

/** @property language defaulted, so a key a newer server adds never fails the sign-in */
@Serializable
data class AuthUserDTO(
    val id: Long,
    val email: String,
    val themeMode: ThemeModeDTO,
    val language: LanguageDTO = LanguageDTO.DEFAULT,
    val demo: Boolean = false,
)

@Serializable
data class AuthResponseDTO(
    val token: String,
    val user: AuthUserDTO,
)
