package sk.mkdigital.kmpshowcase.domain.useCase.analytics

import sk.mkdigital.kmpshowcase.data.analytics.AnalyticsClient

class TrackScreenUseCase(
    private val analyticsClient: AnalyticsClient
) {
    operator fun invoke(screenName: String) {
        analyticsClient.trackScreen(screenName)
    }
}
