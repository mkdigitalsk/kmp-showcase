package sk.mkdigital.kmpshowcase.server.feature.user.service

internal data class User(
    val id: Long,
    val email: String,
    val createdAt: Long,
    val themeMode: ThemeMode,
    val locale: String,
)
