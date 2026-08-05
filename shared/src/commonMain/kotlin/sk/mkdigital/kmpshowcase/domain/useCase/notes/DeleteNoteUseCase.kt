package sk.mkdigital.kmpshowcase.domain.useCase.notes

import sk.mkdigital.kmpshowcase.domain.repository.NoteRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class DeleteNoteUseCase(
    private val noteRepository: NoteRepository
) : UseCase<Long, Unit>() {
    override suspend fun run(params: Long) = noteRepository.delete(params)
}
