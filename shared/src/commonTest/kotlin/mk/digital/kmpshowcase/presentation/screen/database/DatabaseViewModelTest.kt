package mk.digital.kmpshowcase.presentation.screen.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import mk.digital.kmpshowcase.domain.model.Note
import mk.digital.kmpshowcase.domain.model.NoteSortOption
import mk.digital.kmpshowcase.domain.repository.NoteRepository
import mk.digital.kmpshowcase.domain.useCase.notes.DeleteAllNotesUseCase
import mk.digital.kmpshowcase.domain.useCase.notes.DeleteNoteUseCase
import mk.digital.kmpshowcase.domain.useCase.notes.InsertNoteUseCase
import mk.digital.kmpshowcase.domain.useCase.notes.SearchNotesUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DatabaseViewModelTest {

    private class FakeNoteRepository(
        initialNotes: List<Note> = emptyList()
    ) : NoteRepository {
        private val notes = MutableStateFlow(initialNotes)
        private val _insertedNotes = mutableListOf<Note>()
        private val _deletedIds = mutableListOf<Long>()
        private var _deleteAllCalled = false

        val insertedNotes: List<Note> get() = _insertedNotes
        val deletedIds: List<Long> get() = _deletedIds
        val deleteAllCalled: Boolean get() = _deleteAllCalled

        override fun observeAll(sortOption: NoteSortOption): Flow<List<Note>> = notes

        override fun search(query: String, sortOption: NoteSortOption): Flow<List<Note>> {
            return flowOf(notes.value.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
            })
        }

        override suspend fun getById(id: Long): Note? = notes.value.find { it.id == id }

        override suspend fun insert(note: Note) {
            _insertedNotes.add(note)
            notes.value = notes.value + note
        }

        override suspend fun update(note: Note) {
            notes.value = notes.value.map { if (it.id == note.id) note else it }
        }

        override suspend fun delete(id: Long) {
            _deletedIds.add(id)
            notes.value = notes.value.filter { it.id != id }
        }

        override suspend fun deleteAll() {
            _deleteAllCalled = true
            notes.value = emptyList()
        }

        override suspend fun count(): Long = notes.value.size.toLong()
    }

    private fun createViewModel(
        initialNotes: List<Note> = emptyList()
    ): Pair<DatabaseViewModel, FakeNoteRepository> {
        val repository = FakeNoteRepository(initialNotes)
        val viewModel = DatabaseViewModel(
            searchNotesUseCase = SearchNotesUseCase(repository),
            insertNoteUseCase = InsertNoteUseCase(repository),
            deleteNoteUseCase = DeleteNoteUseCase(repository),
            deleteAllNotesUseCase = DeleteAllNotesUseCase(repository)
        )
        return viewModel to repository
    }

    private fun createTestNote(
        id: Long = 1,
        title: String = "Test Note",
        content: String = "Test Content"
    ) = Note(
        id = id,
        title = title,
        content = content,
        createdAt = System.currentTimeMillis()
    )

    // === Default State Tests ===

    @Test
    fun `default state has empty notes list`() {
        val (viewModel, _) = createViewModel()
        assertTrue(viewModel.state.value.notes.isEmpty())
    }

    @Test
    fun `default state has loading true`() {
        val (viewModel, _) = createViewModel()
        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun `default state has no error`() {
        val (viewModel, _) = createViewModel()
        assertFalse(viewModel.state.value.error)
    }

    @Test
    fun `default state has empty search query`() {
        val (viewModel, _) = createViewModel()
        assertEquals("", viewModel.state.value.searchQuery)
    }

    @Test
    fun `default state has DATE_DESC sort option`() {
        val (viewModel, _) = createViewModel()
        assertEquals(NoteSortOption.DATE_DESC, viewModel.state.value.sortOption)
    }

    @Test
    fun `default state has filter menu hidden`() {
        val (viewModel, _) = createViewModel()
        assertFalse(viewModel.state.value.showFilterMenu)
    }

    @Test
    fun `default state has empty new note fields`() {
        val (viewModel, _) = createViewModel()
        assertEquals("", viewModel.state.value.newNoteTitle)
        assertEquals("", viewModel.state.value.newNoteContent)
    }

    // === Search Query Tests ===

    @Test
    fun `onSearchQueryChanged updates search query`() {
        val (viewModel, _) = createViewModel()

        viewModel.onSearchQueryChanged("test query")

        assertEquals("test query", viewModel.state.value.searchQuery)
    }

    // === Sort Option Tests ===

    @Test
    fun `onSortOptionChanged updates sort option`() {
        val (viewModel, _) = createViewModel()

        viewModel.onSortOptionChanged(NoteSortOption.TITLE_ASC)

        assertEquals(NoteSortOption.TITLE_ASC, viewModel.state.value.sortOption)
    }

    @Test
    fun `onSortOptionChanged hides filter menu`() {
        val (viewModel, _) = createViewModel()
        viewModel.toggleFilterMenu() // Open menu first

        viewModel.onSortOptionChanged(NoteSortOption.DATE_ASC)

        assertFalse(viewModel.state.value.showFilterMenu)
    }

    // === Filter Menu Tests ===

    @Test
    fun `toggleFilterMenu opens closed menu`() {
        val (viewModel, _) = createViewModel()

        viewModel.toggleFilterMenu()

        assertTrue(viewModel.state.value.showFilterMenu)
    }

    @Test
    fun `toggleFilterMenu closes open menu`() {
        val (viewModel, _) = createViewModel()
        viewModel.toggleFilterMenu() // Open

        viewModel.toggleFilterMenu() // Close

        assertFalse(viewModel.state.value.showFilterMenu)
    }

    @Test
    fun `dismissFilterMenu closes menu`() {
        val (viewModel, _) = createViewModel()
        viewModel.toggleFilterMenu() // Open

        viewModel.dismissFilterMenu()

        assertFalse(viewModel.state.value.showFilterMenu)
    }

    // === Note Input Tests ===

    @Test
    fun `onTitleChanged updates new note title`() {
        val (viewModel, _) = createViewModel()

        viewModel.onTitleChanged("New Title")

        assertEquals("New Title", viewModel.state.value.newNoteTitle)
    }

    @Test
    fun `onContentChanged updates new note content`() {
        val (viewModel, _) = createViewModel()

        viewModel.onContentChanged("New Content")

        assertEquals("New Content", viewModel.state.value.newNoteContent)
    }

    // === Add Note Tests ===

    @Test
    fun `addNote with blank title does nothing`() {
        val (viewModel, repository) = createViewModel()
        viewModel.onTitleChanged("   ") // Blank title
        viewModel.onContentChanged("Content")

        viewModel.addNote()

        assertTrue(repository.insertedNotes.isEmpty())
    }

    @Test
    fun `addNote with empty title does nothing`() {
        val (viewModel, repository) = createViewModel()
        viewModel.onContentChanged("Content")

        viewModel.addNote()

        assertTrue(repository.insertedNotes.isEmpty())
    }

    // === DatabaseUiState Tests ===

    @Test
    fun `DatabaseUiState default values are correct`() {
        val state = DatabaseUiState()
        assertTrue(state.notes.isEmpty())
        assertTrue(state.isLoading)
        assertFalse(state.error)
        assertEquals("", state.newNoteTitle)
        assertEquals("", state.newNoteContent)
        assertEquals("", state.searchQuery)
        assertEquals(NoteSortOption.DATE_DESC, state.sortOption)
        assertFalse(state.showFilterMenu)
    }

    @Test
    fun `DatabaseUiState can hold notes`() {
        val notes = listOf(createTestNote(1), createTestNote(2))
        val state = DatabaseUiState(notes = notes)
        assertEquals(2, state.notes.size)
    }

    @Test
    fun `DatabaseUiState can have error state`() {
        val state = DatabaseUiState(error = true)
        assertTrue(state.error)
    }

    // === NoteSortOption Tests ===

    @Test
    fun `NoteSortOption has DATE_DESC value`() {
        assertEquals(NoteSortOption.DATE_DESC, NoteSortOption.valueOf("DATE_DESC"))
    }

    @Test
    fun `NoteSortOption has DATE_ASC value`() {
        assertEquals(NoteSortOption.DATE_ASC, NoteSortOption.valueOf("DATE_ASC"))
    }

    @Test
    fun `NoteSortOption has TITLE_ASC value`() {
        assertEquals(NoteSortOption.TITLE_ASC, NoteSortOption.valueOf("TITLE_ASC"))
    }

    @Test
    fun `NoteSortOption has TITLE_DESC value`() {
        assertEquals(NoteSortOption.TITLE_DESC, NoteSortOption.valueOf("TITLE_DESC"))
    }
}
