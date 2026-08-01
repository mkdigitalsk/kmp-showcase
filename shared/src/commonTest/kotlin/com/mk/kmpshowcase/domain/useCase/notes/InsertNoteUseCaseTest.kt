package com.mk.kmpshowcase.domain.useCase.notes

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.model.Note
import com.mk.kmpshowcase.domain.repository.NoteRepository
import com.mk.kmpshowcase.domain.test
import kotlin.test.Test
import kotlin.test.assertFailsWith

class InsertNoteUseCaseTest : BaseTest<InsertNoteUseCase>() {
    override lateinit var classUnderTest: InsertNoteUseCase

    private val noteRepository: NoteRepository = mock()

    override fun beforeEach() {
        classUnderTest = InsertNoteUseCase(noteRepository)
    }

    @Test
    fun `invoke inserts note into repository`() = runTest {
        val note = Note(title = "Test", content = "Content", createdAt = 1000L)

        test(
            given = {
                everySuspend { noteRepository.insert(note) } returns Unit
            },
            whenAction = {
                classUnderTest(note)
            },
            then = {
                verifySuspend { noteRepository.insert(note) }
            }
        )
    }

    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        val note = Note(title = "Test", content = "Content", createdAt = 1000L)
        val exception = RuntimeException("Database error")

        everySuspend { noteRepository.insert(note) } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest(note)
        }
    }
}
