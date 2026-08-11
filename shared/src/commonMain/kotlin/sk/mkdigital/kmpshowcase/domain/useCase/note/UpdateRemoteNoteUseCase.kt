package sk.mkdigital.kmpshowcase.domain.useCase.note

import sk.mkdigital.kmpshowcase.domain.model.RemoteNote
import sk.mkdigital.kmpshowcase.domain.repository.RemoteNoteRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class UpdateRemoteNoteUseCase(
    private val repository: RemoteNoteRepository
) : UseCase<UpdateRemoteNoteUseCase.Params, RemoteNote>() {

    data class Params(val id: Long, val title: String, val content: String, val etag: String)

    override suspend fun run(params: Params): RemoteNote =
        repository.updateNote(params.id, params.title, params.content, params.etag)
}
