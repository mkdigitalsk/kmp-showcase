package com.mk.kmpshowcase.data.dto

import com.mk.kmpshowcase.contracts.auth.AuthResponseDTO
import com.mk.kmpshowcase.domain.model.AuthSession

fun AuthResponseDTO.toAuthSession() = AuthSession(
    token = token,
    userId = user.id,
    email = user.email,
    name = user.name,
)
