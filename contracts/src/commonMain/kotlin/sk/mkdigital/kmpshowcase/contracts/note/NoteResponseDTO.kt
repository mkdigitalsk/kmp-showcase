package sk.mkdigital.kmpshowcase.contracts.note

import kotlinx.serialization.Serializable

@Serializable
data class NoteResponseDTO(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    // Carries its own quotes because they are part of an entity tag ([RFC 9110 §8.8.3][rfc]) and
    // `If-Match` compares strongly, so a client that adds or strips them can never match.
    // A proxy that gzips downgrades the header tag to weak, which is why this one rides in the body.
    //
    // [rfc]: https://www.rfc-editor.org/rfc/rfc9110#section-8.8.3
    val etag: String,
)
