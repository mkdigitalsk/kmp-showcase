package com.mk.kmpshowcase.server.feature.note.api

import com.mk.kmpshowcase.server.feature.note.service.Note
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

fun Note.toDTO() = NoteDTO(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
)
