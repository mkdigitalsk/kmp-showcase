package sk.mkdigital.kmpshowcase.data.crash

interface CrashReporter {
    fun recordException(throwable: Throwable)
    fun log(message: String)

    companion object {
        const val UNKNOWN_ERROR = "Unknown error"
    }
}
