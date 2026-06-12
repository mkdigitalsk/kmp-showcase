package com.mk.kmpshowcase.server.feature.note.service

import com.mk.kmpshowcase.server.feature.note.persistence.NoteRepository

class NoteService(
    private val repository: NoteRepository,
) {
    suspend fun listForUser(userId: Long, query: String? = null): List<Note> =
        if (query.isNullOrBlank()) {
            repository.findAllByUserId(userId)
        } else {
            repository.findByTitleQuery(userId, query)
        }

    suspend fun getById(id: Long, userId: Long): Note? =
        repository.findById(id, userId)

    suspend fun create(userId: Long, title: String, content: String): Note {
        require(title.isNotBlank()) { "Title cannot be blank" }
        return repository.create(userId, title, content)
    }

    suspend fun update(id: Long, userId: Long, title: String, content: String): Note? {
        require(title.isNotBlank()) { "Title cannot be blank" }
        return repository.update(id, userId, title, content)
    }

    suspend fun delete(id: Long, userId: Long): Boolean =
        repository.delete(id, userId)
}
