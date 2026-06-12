package com.mk.kmpshowcase.server.feature.note.persistence

import com.mk.kmpshowcase.server.feature.note.service.Note

interface NoteRepository {
    suspend fun findAllByUserId(userId: Long): List<Note>
    suspend fun findByTitleQuery(userId: Long, query: String): List<Note>
    suspend fun findById(id: Long, userId: Long): Note?
    suspend fun create(userId: Long, title: String, content: String): Note
    suspend fun update(id: Long, userId: Long, title: String, content: String): Note?
    suspend fun delete(id: Long, userId: Long): Boolean
}
