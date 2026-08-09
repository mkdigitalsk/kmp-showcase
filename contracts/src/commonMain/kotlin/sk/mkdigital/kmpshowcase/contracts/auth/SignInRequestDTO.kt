package sk.mkdigital.kmpshowcase.contracts.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequestDTO(
    val email: String,
    val password: String,
)
