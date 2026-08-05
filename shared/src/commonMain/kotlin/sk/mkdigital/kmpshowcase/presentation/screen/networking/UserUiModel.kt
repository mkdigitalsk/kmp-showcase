package sk.mkdigital.kmpshowcase.presentation.screen.networking

import androidx.compose.runtime.Immutable
import sk.mkdigital.kmpshowcase.domain.model.User

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
