package com.mk.kmpshowcase.domain.model

data class AuthSession(
    val token: String,
    val userId: Long,
    val email: String,
    val name: String,
)
