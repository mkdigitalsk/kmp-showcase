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
import com.mk.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DeleteAllNotesUseCaseTest : BaseTest<DeleteAllNotesUseCase>() {
    override lateinit var classUnderTest: DeleteAllNotesUseCase

    private val noteRepository: NoteRepository = mock()

    override fun beforeEach() {
        classUnderTest = DeleteAllNotesUseCase(noteRepository)
    }

    @Test
    fun `invoke deletes all notes from repository`() = runTest {
        test(
            given = {
                everySuspend { noteRepository.deleteAll() } returns Unit
            },
            whenAction = {
                classUnderTest()
            },
            then = {
                verifySuspend { noteRepository.deleteAll() }
            }
        )
    }

    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        val exception = RuntimeException("Database error")

        everySuspend { noteRepository.deleteAll() } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest()
        }
    }
}
