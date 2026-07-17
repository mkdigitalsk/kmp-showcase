package com.mk.kmpshowcase.server.feature.user.api

import com.mk.kmpshowcase.contracts.auth.AuthUserDTO
import com.mk.kmpshowcase.server.feature.user.service.User
import kotlinx.serialization.Serializable

@Serializable
internal data class AcceptInviteRequestDTO(val token: String, val password: String, val name: String? = null)

internal fun User.toAuthUserDTO() =
    AuthUserDTO(id = id, email = email, name = name, themeMode = themeMode.toThemeModeDTO(), locale = locale)
