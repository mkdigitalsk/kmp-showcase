package com.mk.kmpshowcase.contracts.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDTO(
    val email: String,
    val password: String,
)
