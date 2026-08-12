package sk.mkdigital.kmpshowcase.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Suppress("InjectDispatcher")
actual val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
