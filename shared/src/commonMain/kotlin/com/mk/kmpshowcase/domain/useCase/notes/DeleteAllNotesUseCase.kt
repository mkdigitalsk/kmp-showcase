package com.mk.kmpshowcase.domain.useCase.notes

import com.mk.kmpshowcase.domain.repository.NoteRepository
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class DeleteAllNotesUseCase(
    private val noteRepository: NoteRepository
) : UseCase<None, Unit>() {
    override suspend fun run(params: None) = noteRepository.deleteAll()
}
