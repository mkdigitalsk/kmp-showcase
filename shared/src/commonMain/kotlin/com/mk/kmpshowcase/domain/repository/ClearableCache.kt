package com.mk.kmpshowcase.domain.repository

fun interface ClearableCache {
    suspend fun clear()
}
