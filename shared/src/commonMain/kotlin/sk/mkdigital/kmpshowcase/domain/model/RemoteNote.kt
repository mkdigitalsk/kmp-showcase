package sk.mkdigital.kmpshowcase.domain.model

data class RemoteNote(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val etag: String,
)
