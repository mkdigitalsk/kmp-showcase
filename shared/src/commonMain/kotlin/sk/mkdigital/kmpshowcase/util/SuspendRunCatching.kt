package sk.mkdigital.kmpshowcase.util

import kotlin.coroutines.cancellation.CancellationException

// Rethrows cancellation so a cancelled coroutine unwinds, and returns every other throwable as a failure.
@Suppress("TooGenericExceptionCaught")
suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
