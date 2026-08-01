package com.mk.kmpshowcase.server.feature.user.service

internal data class User(
    val id: Long,
    val email: String,
    val name: String,
    val createdAt: Long,
    val themeMode: ThemeMode,
    val locale: String,
)
