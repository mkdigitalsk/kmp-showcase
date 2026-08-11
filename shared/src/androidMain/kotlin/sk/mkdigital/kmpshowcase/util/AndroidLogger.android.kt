package sk.mkdigital.kmpshowcase.util

import android.util.Log
import sk.mkdigital.kmpshowcase.data.crash.CrashReporter

class AndroidLogger(
    private val crashReporter: CrashReporter,
) : Logger {

    override fun e(log: String) {
        Log.e(TAG, log)
    }

    override fun e(e: Throwable) {
        Log.e(TAG, e.stackTraceToString())
        crashReporter.recordException(e)
    }

    override fun e(log: String, e: Throwable) {
        Log.e(TAG, log, e)
        crashReporter.recordException(e)
    }

    override fun d(log: String) {
        Log.d(TAG, log)
    }

    private companion object {
        private const val TAG = "Logger"
    }
}
