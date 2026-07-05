package com.mk.kmpshowcase.presentation.screen.networking

import androidx.compose.runtime.Immutable
import com.mk.kmpshowcase.domain.model.User

@Immutable
data class UserUiModel(
    val id: Long,
    val name: String,
    val email: String,
)

fun User.toUiModel() = UserUiModel(
    id = id,
    name = name,
    email = email,
)
