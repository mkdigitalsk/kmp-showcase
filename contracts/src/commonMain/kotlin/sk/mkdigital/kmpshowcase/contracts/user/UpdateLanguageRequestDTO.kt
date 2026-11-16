package sk.mkdigital.kmpshowcase.contracts.user

import kotlinx.serialization.Serializable
import sk.mkdigital.kmpshowcase.contracts.LanguageDTO

/** @property language the account's pick, which replaces the one it held */
@Serializable
data class UpdateLanguageRequestDTO(
    val language: LanguageDTO,
)
