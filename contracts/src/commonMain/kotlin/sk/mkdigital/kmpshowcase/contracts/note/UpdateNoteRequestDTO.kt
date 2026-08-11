package sk.mkdigital.kmpshowcase.contracts.note

import kotlinx.serialization.Serializable

@Serializable
data class UpdateNoteRequestDTO(
    val title: String,
    val content: String,
)
