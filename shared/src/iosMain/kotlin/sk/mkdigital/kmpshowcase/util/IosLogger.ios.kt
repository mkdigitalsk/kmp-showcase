package sk.mkdigital.kmpshowcase.util

import sk.mkdigital.kmpshowcase.BuildType
import sk.mkdigital.kmpshowcase.data.crash.CrashReporter
import platform.Foundation.NSLog

class IosLogger(
    private val crashReporter: CrashReporter,
    private val buildType: BuildType,
) : Logger {

    override fun e(log: String) {
        NSLog("$TAG: $log")
    }

    override fun e(e: Throwable) {
        NSLog("$TAG ❗️ ${e.message ?: e.toString()}\n${e.stackTraceToString()}")
        crashReporter.recordException(e)
    }

    override fun e(log: String, e: Throwable) {
        NSLog("$TAG ❗️ $log\n${e.message ?: e.toString()}\n${e.stackTraceToString()}")
        crashReporter.recordException(e)
    }

    override fun d(log: String) {
        if (buildType.isDebug) {
            NSLog("$TAG: $log")
        }
    }

    private companion object {
        private const val TAG = "Logger"
    }
}
