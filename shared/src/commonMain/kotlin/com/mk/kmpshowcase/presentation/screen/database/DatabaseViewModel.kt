package com.mk.kmpshowcase.presentation.screen.database

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.mk.kmpshowcase.domain.model.Note
import com.mk.kmpshowcase.domain.model.NoteSortOption
import com.mk.kmpshowcase.domain.useCase.base.invoke
import com.mk.kmpshowcase.domain.useCase.notes.DeleteAllNotesUseCase
import com.mk.kmpshowcase.domain.useCase.notes.DeleteNoteUseCase
import com.mk.kmpshowcase.domain.useCase.notes.InsertNoteUseCase
import com.mk.kmpshowcase.domain.useCase.notes.SearchNotesUseCase
import com.mk.kmpshowcase.presentation.base.BaseViewModel
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

private val SEARCH_DEBOUNCE = 300.milliseconds

class DatabaseViewModel(
    private val searchNotesUseCase: SearchNotesUseCase,
    private val insertNoteUseCase: InsertNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val deleteAllNotesUseCase: DeleteAllNotesUseCase,
) : BaseViewModel<DatabaseUiState>(DatabaseUiState()) {

    private val searchTrigger = MutableStateFlow(SearchTrigger())
    private var searchJob: Job? = null
    private var debounceJob: Job? = null

    @OptIn(FlowPreview::class)
    override fun onResumed() {
        super.onResumed()
        debounceJob = searchTrigger
            .debounce(SEARCH_DEBOUNCE)
            .onEach { trigger -> executeSearch(trigger.query, trigger.sortOption) }
            .launchIn(viewModelScope)

        triggerSearch()
    }

    override fun onPaused() {
        super.onPaused()
        debounceJob?.cancel()
        debounceJob = null
        searchJob?.cancel()
        searchJob = null
    }

    private fun executeSearch(query: String, sortOption: NoteSortOption) {
        searchJob?.cancel()
        searchJob = observe(
            flow = searchNotesUseCase(SearchNotesUseCase.Params(query, sortOption)),
            onEach = { notes ->
                newState { it.copy(notes = notes.map { note -> note.toUiModel() }, isLoading = false) }
            },
            onError = { newState { it.copy(isLoading = false, error = true) } }
        )
    }

    private fun triggerSearch() {
        val currentState = state.value
        searchTrigger.value = SearchTrigger(currentState.searchQuery, currentState.sortOption)
    }

    fun onSearchQueryChanged(query: String) {
        newState { it.copy(searchQuery = query) }
        triggerSearch()
    }

    fun onSortOptionChanged(sortOption: NoteSortOption) {
        newState { it.copy(sortOption = sortOption, showFilterMenu = false) }
        triggerSearch()
    }

    fun toggleFilterMenu() {
        newState { it.copy(showFilterMenu = !it.showFilterMenu) }
    }

    fun dismissFilterMenu() {
        newState { it.copy(showFilterMenu = false) }
    }

    fun onTitleChanged(title: String) {
        newState { it.copy(newNoteTitle = title) }
    }

    fun onContentChanged(content: String) {
        newState { it.copy(newNoteContent = content) }
    }

    fun addNote() {
        val currentState = state.value
        if (currentState.newNoteTitle.isBlank()) return

        val note = Note(
            title = currentState.newNoteTitle.trim(),
            content = currentState.newNoteContent.trim(),
            createdAt = Clock.System.now().toEpochMilliseconds()
        )

        execute(
            action = { insertNoteUseCase(note) },
            onSuccess = { newState { it.copy(newNoteTitle = "", newNoteContent = "") } }
        )
    }

    fun deleteNote(id: Long) {
        execute(action = { deleteNoteUseCase(id) })
    }

    fun deleteAllNotes() {
        execute(action = { deleteAllNotesUseCase() })
    }

    private data class SearchTrigger(
        val query: String = "",
        val sortOption: NoteSortOption = NoteSortOption.DATE_DESC,
    )
}

@Immutable
data class DatabaseUiState(
    val notes: List<NoteUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val error: Boolean = false,
    val newNoteTitle: String = "",
    val newNoteContent: String = "",
    val searchQuery: String = "",
    val sortOption: NoteSortOption = NoteSortOption.DATE_DESC,
    val showFilterMenu: Boolean = false,
)
