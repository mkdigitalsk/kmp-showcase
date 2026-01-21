package mk.digital.kmpshowcase.util

expect object Logger {

    fun e(log: String)

    fun e(e: Throwable)

    fun e(log: String, e: Throwable)

    fun d(log: String)
}
