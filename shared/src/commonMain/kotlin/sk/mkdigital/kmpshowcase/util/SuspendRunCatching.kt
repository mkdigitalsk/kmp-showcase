package sk.mkdigital.kmpshowcase.util

import kotlin.coroutines.cancellation.CancellationException

// runCatching catches CancellationException as well, so a cancelled coroutine reports a failed Result
// and its caller carries on as if the work merely failed.
@Suppress("TooGenericExceptionCaught")
suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
