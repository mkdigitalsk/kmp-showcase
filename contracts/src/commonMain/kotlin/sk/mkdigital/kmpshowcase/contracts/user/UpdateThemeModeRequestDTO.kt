package sk.mkdigital.kmpshowcase.contracts.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateThemeModeRequestDTO(
    val themeMode: ThemeModeDTO,
)
