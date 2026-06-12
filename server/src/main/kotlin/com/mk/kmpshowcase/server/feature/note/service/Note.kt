package com.mk.kmpshowcase.server.feature.note.service

data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long,
)
