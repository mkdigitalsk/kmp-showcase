package sk.mkdigital.kmpshowcase.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import sk.mkdigital.kmpshowcase.data.database.AppDatabase
import sk.mkdigital.kmpshowcase.data.local.database.transform
import sk.mkdigital.kmpshowcase.data.local.database.transformAll
import sk.mkdigital.kmpshowcase.domain.model.Note
import sk.mkdigital.kmpshowcase.domain.model.NoteSortOption
import sk.mkdigital.kmpshowcase.domain.repository.NoteRepository
import sk.mkdigital.kmpshowcase.util.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NoteRepositoryImpl(
    database: AppDatabase,
    private val dispatchers: DispatcherProvider,
) : NoteRepository {

    private val queries = database.noteQueries

    override fun observeAll(sortOption: NoteSortOption): Flow<List<Note>> {
        val query = when (sortOption) {
            NoteSortOption.DATE_DESC -> queries.selectAll()
            NoteSortOption.DATE_ASC -> queries.selectAllByDateAsc()
            NoteSortOption.TITLE_ASC -> queries.selectAllByTitleAsc()
            NoteSortOption.TITLE_DESC -> queries.selectAllByTitleDesc()
        }
        return query.asFlow().mapToList(dispatchers.io).map { it.transformAll() }
    }

    override fun search(query: String, sortOption: NoteSortOption): Flow<List<Note>> {
        val dbQuery = when (sortOption) {
            NoteSortOption.DATE_DESC -> queries.search(query, query)
            NoteSortOption.DATE_ASC -> queries.searchByDateAsc(query, query)
            NoteSortOption.TITLE_ASC -> queries.searchByTitleAsc(query, query)
            NoteSortOption.TITLE_DESC -> queries.searchByTitleDesc(query, query)
        }
        return dbQuery.asFlow().mapToList(dispatchers.io).map { it.transformAll() }
    }

    override suspend fun getById(id: Long): Note? = withContext(dispatchers.io) {
        queries.selectById(id).executeAsOneOrNull()?.transform()
    }

    override suspend fun insert(note: Note): Unit = withContext(dispatchers.io) {
        queries.insert(note.title, note.content, note.createdAt)
    }

    override suspend fun update(note: Note): Unit = withContext(dispatchers.io) {
        queries.update(note.title, note.content, note.id)
    }

    override suspend fun delete(id: Long): Unit = withContext(dispatchers.io) {
        queries.deleteById(id)
    }

    override suspend fun deleteAll(): Unit = withContext(dispatchers.io) {
        queries.deleteAll()
    }

    override suspend fun count(): Long = withContext(dispatchers.io) {
        queries.count().executeAsOne()
    }
}
