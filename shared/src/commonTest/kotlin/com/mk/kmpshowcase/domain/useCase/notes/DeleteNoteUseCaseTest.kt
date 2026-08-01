package com.mk.kmpshowcase.domain.useCase.notes

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.repository.NoteRepository
import com.mk.kmpshowcase.domain.test
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DeleteNoteUseCaseTest : BaseTest<DeleteNoteUseCase>() {
    override lateinit var classUnderTest: DeleteNoteUseCase

    private val noteRepository: NoteRepository = mock()

    override fun beforeEach() {
        classUnderTest = DeleteNoteUseCase(noteRepository)
    }

    @Test
    fun `invoke deletes note from repository`() = runTest {
        val noteId = 1L

        test(
            given = {
                everySuspend { noteRepository.delete(noteId) } returns Unit
            },
            whenAction = {
                classUnderTest(noteId)
            },
            then = {
                verifySuspend { noteRepository.delete(noteId) }
            }
        )
    }

    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        val noteId = 1L
        val exception = RuntimeException("Database error")

        everySuspend { noteRepository.delete(noteId) } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest(noteId)
        }
    }
}
