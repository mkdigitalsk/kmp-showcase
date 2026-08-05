package sk.mkdigital.kmpshowcase.data.local.database

import sk.mkdigital.kmpshowcase.domain.model.RegisteredUser
import sk.mkdigital.kmpshowcase.data.database.RegisteredUser as RegisteredUserEntity

fun RegisteredUserEntity.transform() = RegisteredUser(
    id = id,
    name = name,
    email = email,
    password = password,
    createdAt = createdAt
)
