package com.mk.kmpshowcase.fake

import com.mk.kmpshowcase.data.analytics.AnalyticsClient

object NoOpAnalyticsClient : AnalyticsClient {
    override fun trackScreen(screenName: String) = Unit
    override fun recordException(throwable: Throwable) = Unit
    override fun log(message: String) = Unit
}
