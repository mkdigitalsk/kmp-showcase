package sk.mkdigital.kmpshowcase.contracts.user

import kotlinx.serialization.Serializable
import sk.mkdigital.kmpshowcase.contracts.ThemeModeDTO

@Serializable
data class UpdateThemeModeRequestDTO(
    val themeMode: ThemeModeDTO,
)
