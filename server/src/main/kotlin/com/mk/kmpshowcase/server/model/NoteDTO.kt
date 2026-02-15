package com.mk.kmpshowcase.server.model

import kotlinx.serialization.Serializable

@Serializable
data class NoteDTO(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long,
)

@Serializable
data class CreateNoteRequest(
    val title: String,
    val content: String,
)

@Serializable
data class UpdateNoteRequest(
    val title: String,
    val content: String,
)
