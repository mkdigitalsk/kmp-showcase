package sk.mkdigital.kmpshowcase.server.feature.note.api

import sk.mkdigital.kmpshowcase.contracts.note.NoteResponseDTO
import sk.mkdigital.kmpshowcase.server.feature.note.service.Note

internal fun Note.toNoteResponseDTO() = NoteResponseDTO(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    etag = version.toEtag(),
)

internal fun Long.toEtag() = "\"$this\""

/**
 * The versions an `If-Match` names, empty when it names none this row could satisfy — which is a
 * precondition that fails, never one that is skipped.
 *
 * `*` is dropped rather than honoured: it matches whenever the row exists, which is last-write-wins
 * wearing the precondition's uniform. A `W/` tag is dropped because `If-Match` compares strongly, so a
 * weak tag can never match — and a gzipping proxy turns strong tags weak in transit.
 */
internal fun String.toExpectedVersions(): Set<Long> =
    split(',')
        .map { it.trim() }
        .mapNotNull { tag -> tag.removeSurrounding("\"").takeIf { tag.startsWith('"') && tag.endsWith('"') } }
        .mapNotNull { it.toLongOrNull() }
        .toSet()
