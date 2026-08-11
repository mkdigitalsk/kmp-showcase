package sk.mkdigital.kmpshowcase.domain.useCase.note

import sk.mkdigital.kmpshowcase.domain.model.RemoteNote
import sk.mkdigital.kmpshowcase.domain.repository.RemoteNoteRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class CreateRemoteNoteUseCase(
    private val repository: RemoteNoteRepository
) : UseCase<CreateRemoteNoteUseCase.Params, RemoteNote>() {

    data class Params(val title: String, val content: String)

    override suspend fun run(params: Params): RemoteNote =
        repository.createNote(params.title, params.content)
}
