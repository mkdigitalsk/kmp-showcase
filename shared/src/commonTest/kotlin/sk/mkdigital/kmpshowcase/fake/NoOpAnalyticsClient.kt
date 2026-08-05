package sk.mkdigital.kmpshowcase.fake

import sk.mkdigital.kmpshowcase.data.analytics.AnalyticsClient

object NoOpAnalyticsClient : AnalyticsClient {
    override fun trackScreen(screenName: String) = Unit
    override fun recordException(throwable: Throwable) = Unit
    override fun log(message: String) = Unit
}
