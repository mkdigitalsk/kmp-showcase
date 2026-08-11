package sk.mkdigital.kmpshowcase.domain.useCase.note

import sk.mkdigital.kmpshowcase.domain.model.RemoteNote
import sk.mkdigital.kmpshowcase.domain.repository.RemoteNoteRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class GetRemoteNotesUseCase(
    private val repository: RemoteNoteRepository
) : UseCase<None, List<RemoteNote>>() {
    override suspend fun run(params: None): List<RemoteNote> = repository.getNotes()
}
