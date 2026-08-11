package sk.mkdigital.kmpshowcase.server.feature.note.service

internal data class Note(
    val id: Long,
    val ownerId: Long,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val version: Long,
)
