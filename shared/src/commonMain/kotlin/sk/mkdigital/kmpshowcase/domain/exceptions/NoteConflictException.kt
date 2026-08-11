package sk.mkdigital.kmpshowcase.domain.exceptions

import sk.mkdigital.kmpshowcase.domain.exceptions.base.BaseException
import sk.mkdigital.kmpshowcase.domain.model.RemoteNote

class NoteConflictException(
    val current: RemoteNote,
    cause: Throwable? = null,
) : BaseException("The note changed on the server", cause) {
    override val errorCode: String = "2412"
    override val logMessage: String = "The note was edited elsewhere while this edit was open"
    override val shouldReport: Boolean = false
}
