package com.mk.kmpshowcase.domain.useCase.notes

import com.mk.kmpshowcase.domain.repository.NoteRepository
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class DeleteNoteUseCase(
    private val noteRepository: NoteRepository
) : UseCase<Long, Unit>() {
    override suspend fun run(params: Long) = noteRepository.delete(params)
}
