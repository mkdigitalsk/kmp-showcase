package sk.mkdigital.kmpshowcase.server.feature.user.api

import sk.mkdigital.kmpshowcase.contracts.LanguageDTO
import sk.mkdigital.kmpshowcase.contracts.ThemeModeDTO
import sk.mkdigital.kmpshowcase.contracts.user.UserResponseDTO
import sk.mkdigital.kmpshowcase.server.feature.user.service.Language
import sk.mkdigital.kmpshowcase.server.feature.user.service.ThemeMode
import sk.mkdigital.kmpshowcase.server.feature.user.service.User

internal fun User.toUserResponseDTO() = UserResponseDTO(
    id = id,
    email = email,
    createdAt = createdAt,
    themeMode = themeMode.toThemeModeDTO(),
    language = language.toLanguageDTO(),
    demo = demo,
)

internal fun ThemeMode.toThemeModeDTO(): ThemeModeDTO = when (this) {
    ThemeMode.SYSTEM -> ThemeModeDTO.SYSTEM
    ThemeMode.LIGHT -> ThemeModeDTO.LIGHT
    ThemeMode.DARK -> ThemeModeDTO.DARK
}

internal fun ThemeModeDTO.toThemeMode(): ThemeMode = when (this) {
    ThemeModeDTO.SYSTEM -> ThemeMode.SYSTEM
    ThemeModeDTO.LIGHT -> ThemeMode.LIGHT
    ThemeModeDTO.DARK -> ThemeMode.DARK
}

internal fun Language.toLanguageDTO(): LanguageDTO = when (this) {
    Language.SK -> LanguageDTO.SK
    Language.EN -> LanguageDTO.EN
}

internal fun LanguageDTO.toLanguage(): Language = when (this) {
    LanguageDTO.SK -> Language.SK
    LanguageDTO.EN -> Language.EN
}
