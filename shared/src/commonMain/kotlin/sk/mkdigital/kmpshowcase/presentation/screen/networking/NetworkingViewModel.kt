package sk.mkdigital.kmpshowcase.presentation.screen.networking

import androidx.compose.runtime.Immutable
import sk.mkdigital.kmpshowcase.domain.exceptions.NoteConflictException
import sk.mkdigital.kmpshowcase.domain.useCase.base.invoke
import sk.mkdigital.kmpshowcase.domain.useCase.note.CreateRemoteNoteUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.note.DeleteRemoteNoteUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.note.GetRemoteNotesUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.note.UpdateRemoteNoteUseCase
import sk.mkdigital.kmpshowcase.presentation.base.AppError
import sk.mkdigital.kmpshowcase.presentation.base.BaseViewModel
import sk.mkdigital.kmpshowcase.presentation.base.toAppError

class NetworkingViewModel(
    private val getRemoteNotesUseCase: GetRemoteNotesUseCase,
    private val createRemoteNoteUseCase: CreateRemoteNoteUseCase,
    private val updateRemoteNoteUseCase: UpdateRemoteNoteUseCase,
    private val deleteRemoteNoteUseCase: DeleteRemoteNoteUseCase,
) : BaseViewModel<NetworkingUiState>(NetworkingUiState()) {

    override fun loadInitialData() {
        fetchNotes()
    }

    fun fetchNotes() {
        execute(
            action = { getRemoteNotesUseCase() },
            onLoading = { newState { it.copy(isLoading = true, error = null) } },
            onSuccess = { notes ->
                newState { it.copy(isLoading = false, notes = notes.map { note -> note.toUiModel() }) }
            },
            onError = { error -> newState { it.copy(isLoading = false, error = error.toAppError()) } },
        )
    }

    fun refresh() = fetchNotes()

    fun createNote(title: String, content: String) {
        execute(
            action = { createRemoteNoteUseCase(CreateRemoteNoteUseCase.Params(title, content)) },
            onLoading = { newState { it.copy(isSaving = true, error = null) } },
            onSuccess = { newState { it.copy(isSaving = false) }; fetchNotes() },
            onError = { error -> newState { it.copy(isSaving = false, error = error.toAppError()) } },
        )
    }

    // The tag comes from the row as it was read, never re-read at save — re-reading would adopt whatever
    // landed meanwhile and overwrite an edit this person never saw.
    fun updateNote(id: Long, title: String, content: String, etag: String) {
        execute(
            action = { updateRemoteNoteUseCase(UpdateRemoteNoteUseCase.Params(id, title, content, etag)) },
            onLoading = { newState { it.copy(isSaving = true, error = null, conflict = null) } },
            onSuccess = { newState { it.copy(isSaving = false, editing = null) }; fetchNotes() },
            onError = { error ->
                newState {
                    if (error is NoteConflictException) {
                        it.copy(isSaving = false, conflict = error.current.toUiModel())
                    } else {
                        it.copy(isSaving = false, error = error.toAppError())
                    }
                }
            },
        )
    }

    fun deleteNote(id: Long) {
        execute(
            action = { deleteRemoteNoteUseCase(id) },
            onSuccess = { fetchNotes() },
            onError = { error -> newState { it.copy(error = error.toAppError()) } },
        )
    }

    fun startEditing(note: RemoteNoteUiModel) = newState { it.copy(editing = note) }

    fun cancelEditing() = newState { it.copy(editing = null, conflict = null) }

    /** Keep this edit and retry against the tag the server just returned — the only one that can now match. */
    fun overwriteConflict(title: String, content: String) {
        val conflict = state.value.conflict ?: return
        updateNote(conflict.id, title, content, conflict.etag)
    }

    /** Take the server's version and drop this edit. */
    fun discardConflict() = newState { it.copy(conflict = null, editing = null) }
}

@Immutable
data class NetworkingUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val notes: List<RemoteNoteUiModel> = emptyList(),
    val editing: RemoteNoteUiModel? = null,
    val conflict: RemoteNoteUiModel? = null,
    val error: AppError? = null,
)
