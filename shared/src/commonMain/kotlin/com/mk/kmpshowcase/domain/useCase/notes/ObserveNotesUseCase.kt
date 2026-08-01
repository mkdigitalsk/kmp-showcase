package com.mk.kmpshowcase.domain.useCase.notes

import kotlinx.coroutines.flow.Flow
import com.mk.kmpshowcase.domain.model.Note
import com.mk.kmpshowcase.domain.repository.NoteRepository
import com.mk.kmpshowcase.domain.useCase.base.FlowUseCase
import com.mk.kmpshowcase.domain.useCase.base.None

class ObserveNotesUseCase(
    private val noteRepository: NoteRepository
) : FlowUseCase<None, List<Note>>() {
    override fun run(params: None): Flow<List<Note>> = noteRepository.observeAll()
}
