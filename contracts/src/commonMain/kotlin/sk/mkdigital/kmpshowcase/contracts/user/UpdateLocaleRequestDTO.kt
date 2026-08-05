package sk.mkdigital.kmpshowcase.contracts.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateLocaleRequestDTO(
    val locale: String,
)
