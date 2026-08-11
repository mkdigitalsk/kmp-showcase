package sk.mkdigital.kmpshowcase.domain.useCase.note

import sk.mkdigital.kmpshowcase.domain.repository.RemoteNoteRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class DeleteRemoteNoteUseCase(
    private val repository: RemoteNoteRepository
) : UseCase<Long, Unit>() {
    override suspend fun run(params: Long) = repository.deleteNote(params)
}
