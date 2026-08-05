package sk.mkdigital.kmpshowcase.contracts.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDTO(
    val email: String,
    val password: String,
    val name: String,
)
