package sk.mkdigital.kmpshowcase.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatcherProvider {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
}

/** Kotlin/Native declares `Dispatchers.IO` internal, so the blocking-work dispatcher is per platform. */
expect val ioDispatcher: CoroutineDispatcher

@Suppress("InjectDispatcher")
class DefaultDispatcherProvider : DispatcherProvider {
    override val io: CoroutineDispatcher = ioDispatcher
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val main: CoroutineDispatcher = Dispatchers.Main
}
