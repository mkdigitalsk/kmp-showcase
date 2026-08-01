package com.mk.kmpshowcase.domain.model

data class RegisteredUser(
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String,
    val createdAt: Long,
)
