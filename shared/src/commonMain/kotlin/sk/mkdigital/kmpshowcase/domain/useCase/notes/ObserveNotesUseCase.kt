package sk.mkdigital.kmpshowcase.domain.useCase.notes

import kotlinx.coroutines.flow.Flow
import sk.mkdigital.kmpshowcase.domain.model.Note
import sk.mkdigital.kmpshowcase.domain.repository.NoteRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.FlowUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.base.None

class ObserveNotesUseCase(
    private val noteRepository: NoteRepository
) : FlowUseCase<None, List<Note>>() {
    override fun run(params: None): Flow<List<Note>> = noteRepository.observeAll()
}
