package sk.mkdigital.kmpshowcase.domain.useCase.notes

import sk.mkdigital.kmpshowcase.domain.model.Note
import sk.mkdigital.kmpshowcase.domain.repository.NoteRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class InsertNoteUseCase(
    private val noteRepository: NoteRepository
) : UseCase<Note, Unit>() {
    override suspend fun run(params: Note) = noteRepository.insert(params)
}
