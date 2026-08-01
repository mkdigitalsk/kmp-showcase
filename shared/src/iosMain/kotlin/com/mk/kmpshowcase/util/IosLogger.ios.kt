package com.mk.kmpshowcase.util

import com.mk.kmpshowcase.data.analytics.AnalyticsClient
import platform.Foundation.NSLog

class IosLogger(
    private val analyticsClient: AnalyticsClient,
) : Logger {

    override fun e(log: String) {
        NSLog("$TAG: $log")
    }

    override fun e(e: Throwable) {
        NSLog("$TAG ❗️ ${e.message ?: e.toString()}\n${e.stackTraceToString()}")
        analyticsClient.recordException(e)
    }

    override fun e(log: String, e: Throwable) {
        NSLog("$TAG ❗️ $log\n${e.message ?: e.toString()}\n${e.stackTraceToString()}")
        analyticsClient.recordException(e)
    }

    override fun d(log: String) {
        NSLog("$TAG: $log")
    }

    private companion object {
        private const val TAG = "Logger"
    }
}
