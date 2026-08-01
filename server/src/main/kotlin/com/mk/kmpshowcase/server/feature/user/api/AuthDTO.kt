package com.mk.kmpshowcase.server.feature.user.api

import com.mk.kmpshowcase.contracts.auth.AuthUserDTO
import com.mk.kmpshowcase.server.feature.user.service.User
import kotlinx.serialization.Serializable

internal fun User.toAuthUserDTO() =
    AuthUserDTO(id = id, email = email, name = name, themeMode = themeMode.toThemeModeDTO(), locale = locale)
