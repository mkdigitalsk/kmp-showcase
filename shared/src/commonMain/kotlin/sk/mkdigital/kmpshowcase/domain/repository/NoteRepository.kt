package sk.mkdigital.kmpshowcase.domain.repository

import kotlinx.coroutines.flow.Flow
import sk.mkdigital.kmpshowcase.domain.model.Note
import sk.mkdigital.kmpshowcase.domain.model.NoteSortOption

interface NoteRepository {
    fun observeAll(sortOption: NoteSortOption = NoteSortOption.DATE_DESC): Flow<List<Note>>
    fun search(query: String, sortOption: NoteSortOption = NoteSortOption.DATE_DESC): Flow<List<Note>>
    suspend fun getById(id: Long): Note?
    suspend fun insert(note: Note)
    suspend fun update(note: Note)
    suspend fun delete(id: Long)
    suspend fun deleteAll()
    suspend fun count(): Long
}
