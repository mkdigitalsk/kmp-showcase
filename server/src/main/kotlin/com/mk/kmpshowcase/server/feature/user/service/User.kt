package com.mk.kmpshowcase.server.feature.user.service

data class User(
    val id: Long,
    val email: String,
    val name: String,
    val createdAt: Long,
)
