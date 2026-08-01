package com.mk.kmpshowcase.presentation.screen.database

import com.mk.kmpshowcase.domain.model.NoteSortOption
import com.mk.kmpshowcase.domain.useCase.notes.DeleteAllNotesUseCase
import com.mk.kmpshowcase.domain.useCase.notes.DeleteNoteUseCase
import com.mk.kmpshowcase.domain.useCase.notes.InsertNoteUseCase
import com.mk.kmpshowcase.domain.useCase.notes.SearchNotesUseCase
import com.mk.kmpshowcase.presentation.base.BaseViewModelTest
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.not
import dev.mokkery.verifySuspend
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DatabaseViewModelTest : BaseViewModelTest() {

    private val searchNotesUseCase = mock<SearchNotesUseCase>()
    private val insertNoteUseCase = mock<InsertNoteUseCase>()
    private val deleteNoteUseCase = mock<DeleteNoteUseCase>()
    private val deleteAllNotesUseCase = mock<DeleteAllNotesUseCase>()

    private fun createViewModel() = DatabaseViewModel(
        searchNotesUseCase = searchNotesUseCase,
        insertNoteUseCase = insertNoteUseCase,
        deleteNoteUseCase = deleteNoteUseCase,
        deleteAllNotesUseCase = deleteAllNotesUseCase,
    )

    @Test
    fun `default state has empty notes list`() {
        assertTrue(createViewModel().state.value.notes.isEmpty())
    }

    @Test
    fun `default state has loading true`() {
        assertTrue(createViewModel().state.value.isLoading)
    }

    @Test
    fun `default state has no error`() {
        assertFalse(createViewModel().state.value.error)
    }

    @Test
    fun `default state has empty search query`() {
        assertEquals("", createViewModel().state.value.searchQuery)
    }

    @Test
    fun `default state has DATE_DESC sort option`() {
        assertEquals(NoteSortOption.DATE_DESC, createViewModel().state.value.sortOption)
    }

    @Test
    fun `default state has filter menu hidden`() {
        assertFalse(createViewModel().state.value.showFilterMenu)
    }

    @Test
    fun `default state has empty new note fields`() {
        val state = createViewModel().state.value
        assertEquals("", state.newNoteTitle)
        assertEquals("", state.newNoteContent)
    }

    @Test
    fun `onSearchQueryChanged updates search query`() {
        val viewModel = createViewModel()

        viewModel.onSearchQueryChanged("test query")

        assertEquals("test query", viewModel.state.value.searchQuery)
    }

    @Test
    fun `onSortOptionChanged updates sort option`() {
        val viewModel = createViewModel()

        viewModel.onSortOptionChanged(NoteSortOption.TITLE_ASC)

        assertEquals(NoteSortOption.TITLE_ASC, viewModel.state.value.sortOption)
    }

    @Test
    fun `onSortOptionChanged hides filter menu`() {
        val viewModel = createViewModel()
        viewModel.toggleFilterMenu() // Open menu first

        viewModel.onSortOptionChanged(NoteSortOption.DATE_ASC)

        assertFalse(viewModel.state.value.showFilterMenu)
    }

    @Test
    fun `toggleFilterMenu opens closed menu`() {
        val viewModel = createViewModel()

        viewModel.toggleFilterMenu()

        assertTrue(viewModel.state.value.showFilterMenu)
    }

    @Test
    fun `toggleFilterMenu closes open menu`() {
        val viewModel = createViewModel()
        viewModel.toggleFilterMenu() // Open

        viewModel.toggleFilterMenu() // Close

        assertFalse(viewModel.state.value.showFilterMenu)
    }

    @Test
    fun `dismissFilterMenu closes menu`() {
        val viewModel = createViewModel()
        viewModel.toggleFilterMenu() // Open

        viewModel.dismissFilterMenu()

        assertFalse(viewModel.state.value.showFilterMenu)
    }

    @Test
    fun `onTitleChanged updates new note title`() {
        val viewModel = createViewModel()

        viewModel.onTitleChanged("New Title")

        assertEquals("New Title", viewModel.state.value.newNoteTitle)
    }

    @Test
    fun `onContentChanged updates new note content`() {
        val viewModel = createViewModel()

        viewModel.onContentChanged("New Content")

        assertEquals("New Content", viewModel.state.value.newNoteContent)
    }

    @Test
    fun `addNote with blank title does not insert`() {
        val viewModel = createViewModel()
        viewModel.onTitleChanged("   ") // Blank title
        viewModel.onContentChanged("Content")

        viewModel.addNote()

        verifySuspend(not) { insertNoteUseCase(any()) }
    }

    @Test
    fun `addNote with empty title does not insert`() {
        val viewModel = createViewModel()
        viewModel.onContentChanged("Content")

        viewModel.addNote()

        verifySuspend(not) { insertNoteUseCase(any()) }
    }

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
        val notes = listOf(
            NoteUiModel(id = 1, title = "Test Note", content = "Test Content", createdAt = "2009-02-13 23:31"),
            NoteUiModel(id = 2, title = "Test Note", content = "Test Content", createdAt = "2009-02-13 23:31"),
        )
        val state = DatabaseUiState(notes = notes)
        assertEquals(2, state.notes.size)
    }

    @Test
    fun `DatabaseUiState can have error state`() {
        val state = DatabaseUiState(error = true)
        assertTrue(state.error)
    }

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
