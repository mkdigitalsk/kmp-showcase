package sk.mkdigital.kmpshowcase.domain.useCase.notes

import sk.mkdigital.kmpshowcase.domain.repository.NoteRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class DeleteAllNotesUseCase(
    private val noteRepository: NoteRepository
) : UseCase<None, Unit>() {
    override suspend fun run(params: None) = noteRepository.deleteAll()
}
