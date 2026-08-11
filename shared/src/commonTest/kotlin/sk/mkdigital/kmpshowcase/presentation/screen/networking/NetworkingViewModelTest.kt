package sk.mkdigital.kmpshowcase.presentation.screen.networking

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import sk.mkdigital.kmpshowcase.domain.exceptions.NoteConflictException
import sk.mkdigital.kmpshowcase.domain.model.RemoteNote
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.note.CreateRemoteNoteUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.note.DeleteRemoteNoteUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.note.GetRemoteNotesUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.note.UpdateRemoteNoteUseCase
import sk.mkdigital.kmpshowcase.presentation.base.BaseViewModelTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkingViewModelTest : BaseViewModelTest() {

    private val getNotes = mock<GetRemoteNotesUseCase>()
    private val createNote = mock<CreateRemoteNoteUseCase>()
    private val updateNote = mock<UpdateRemoteNoteUseCase>()
    private val deleteNote = mock<DeleteRemoteNoteUseCase>()

    private val milk = RemoteNote(
        id = 1,
        title = "Buy milk",
        content = "two litres",
        createdAt = 0,
        updatedAt = 0,
        etag = "\"0\"",
    )

    private fun viewModel() = NetworkingViewModel(getNotes, createNote, updateNote, deleteNote)

    @Test
    fun `fetching maps domain notes into UI models`() = runTest {
        everySuspend { getNotes(None) } returns listOf(milk)

        val viewModel = viewModel()
        viewModel.fetchNotes()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(listOf(RemoteNoteUiModel(id = 1, title = "Buy milk", content = "two litres", etag = "\"0\"")), state.notes)
    }

    @Test
    fun `a failed fetch sets the error and stops loading`() = runTest {
        everySuspend { getNotes(None) } throws RuntimeException("boom")

        val viewModel = viewModel()
        viewModel.fetchNotes()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
    }

    @Test
    fun `a refused write surfaces the server's row rather than an error`() = runTest {
        val theirs = milk.copy(title = "Someone else won", etag = "\"7\"")
        everySuspend { updateNote(any()) } throws NoteConflictException(theirs)
        everySuspend { getNotes(None) } returns listOf(milk)

        val viewModel = viewModel()
        viewModel.updateNote(id = 1, title = "Mine", content = "mine", etag = "\"0\"")

        val state = viewModel.state.value
        assertEquals("Someone else won", state.conflict?.title)
        assertNull(state.error, "a conflict is someone else saving first, not a failure to report")
    }

    @Test
    fun `keeping mine retries against the tag the server returned`() = runTest {
        val theirs = milk.copy(title = "Someone else won", etag = "\"7\"")
        everySuspend { updateNote(UpdateRemoteNoteUseCase.Params(1, "Mine", "mine", "\"0\"")) } throws
            NoteConflictException(theirs)
        everySuspend { updateNote(UpdateRemoteNoteUseCase.Params(1, "Mine", "mine", "\"7\"")) } returns
            milk.copy(title = "Mine", etag = "\"8\"")
        everySuspend { getNotes(None) } returns listOf(milk.copy(title = "Mine", etag = "\"8\""))

        val viewModel = viewModel()
        viewModel.updateNote(id = 1, title = "Mine", content = "mine", etag = "\"0\"")
        viewModel.overwriteConflict(title = "Mine", content = "mine")

        assertNull(viewModel.state.value.conflict)
    }
}
