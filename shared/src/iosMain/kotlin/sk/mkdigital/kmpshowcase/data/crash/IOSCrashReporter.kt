package sk.mkdigital.kmpshowcase.data.crash

class IOSCrashReporter : CrashReporter {

    override fun recordException(throwable: Throwable) {
        exceptionHandler?.invoke(
            throwable.message ?: CrashReporter.UNKNOWN_ERROR,
            throwable.stackTraceToString()
        )
    }

    override fun log(message: String) {
        logHandler?.invoke(message)
    }

    companion object {
        var exceptionHandler: ((message: String, stackTrace: String) -> Unit)? = null
        var logHandler: ((String) -> Unit)? = null
    }
}
