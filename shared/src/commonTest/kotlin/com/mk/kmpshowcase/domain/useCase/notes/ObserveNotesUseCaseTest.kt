package com.mk.kmpshowcase.domain.useCase.notes

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.model.Note
import com.mk.kmpshowcase.domain.repository.NoteRepository
import com.mk.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveNotesUseCaseTest : BaseTest<ObserveNotesUseCase>() {
    override lateinit var classUnderTest: ObserveNotesUseCase

    private val noteRepository: NoteRepository = mock()

    override fun beforeEach() {
        classUnderTest = ObserveNotesUseCase(noteRepository)
    }

    @Test
    fun `invoke returns flow of notes from repository`() = runTest {
        val notes = listOf(
            Note(id = 1, title = "Note 1", content = "Content 1", createdAt = 1000L),
            Note(id = 2, title = "Note 2", content = "Content 2", createdAt = 2000L)
        )

        every { noteRepository.observeAll() } returns flowOf(notes)

        val result = classUnderTest().first()

        assertEquals(notes, result)
        verify { noteRepository.observeAll() }
    }

    @Test
    fun `invoke returns empty list when no notes exist`() = runTest {
        every { noteRepository.observeAll() } returns flowOf(emptyList())

        val result = classUnderTest().first()

        assertEquals(emptyList(), result)
    }
}
