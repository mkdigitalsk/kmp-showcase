package com.mk.kmpshowcase.data.analytics

class IOSAnalyticsClient : AnalyticsClient {

    override fun trackScreen(screenName: String) {
        screenTrackingHandler?.invoke(screenName)
    }

    override fun recordException(throwable: Throwable) {
        exceptionHandler?.invoke(
            throwable.message ?: AnalyticsClient.UNKNOWN_ERROR,
            throwable.stackTraceToString()
        )
    }

    override fun log(message: String) {
        logHandler?.invoke(message)
    }

    companion object {
        var screenTrackingHandler: ((String) -> Unit)? = null
        var exceptionHandler: ((message: String, stackTrace: String) -> Unit)? = null
        var logHandler: ((String) -> Unit)? = null
    }
}
