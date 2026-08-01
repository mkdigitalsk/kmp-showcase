package com.mk.kmpshowcase.data.local.database

import com.mk.kmpshowcase.domain.model.RegisteredUser
import com.mk.kmpshowcase.data.database.RegisteredUser as RegisteredUserEntity

fun RegisteredUserEntity.transform() = RegisteredUser(
    id = id,
    name = name,
    email = email,
    password = password,
    createdAt = createdAt
)
