package sk.mkdigital.kmpshowcase.server.feature.user.api

import sk.mkdigital.kmpshowcase.contracts.auth.AuthUserDTO
import sk.mkdigital.kmpshowcase.server.feature.user.service.User

internal fun User.toAuthUserDTO() =
    AuthUserDTO(id = id, email = email, themeMode = themeMode.toThemeModeDTO(), locale = locale)
