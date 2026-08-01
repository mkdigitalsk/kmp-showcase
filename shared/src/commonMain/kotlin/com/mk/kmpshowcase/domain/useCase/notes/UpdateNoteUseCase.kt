package com.mk.kmpshowcase.domain.useCase.notes

import com.mk.kmpshowcase.domain.model.Note
import com.mk.kmpshowcase.domain.repository.NoteRepository
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class UpdateNoteUseCase(
    private val noteRepository: NoteRepository
) : UseCase<Note, Unit>() {
    override suspend fun run(params: Note) = noteRepository.update(params)
}
