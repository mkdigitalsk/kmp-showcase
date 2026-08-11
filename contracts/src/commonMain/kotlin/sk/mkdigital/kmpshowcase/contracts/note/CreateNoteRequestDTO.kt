package sk.mkdigital.kmpshowcase.contracts.note

import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteRequestDTO(
    val title: String,
    val content: String,
)
