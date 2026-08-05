package sk.mkdigital.kmpshowcase.data.local.database

import sk.mkdigital.kmpshowcase.domain.model.Note
import sk.mkdigital.kmpshowcase.data.database.Note as NoteEntity

fun NoteEntity.transform() = Note(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt
)

fun List<NoteEntity>.transformAll() = map(NoteEntity::transform)
