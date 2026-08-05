package sk.mkdigital.kmpshowcase.data.dto

import sk.mkdigital.kmpshowcase.contracts.auth.AuthResponseDTO
import sk.mkdigital.kmpshowcase.domain.model.AuthSession

fun AuthResponseDTO.toAuthSession() = AuthSession(
    token = token,
    userId = user.id,
    email = user.email,
    name = user.name,
)
