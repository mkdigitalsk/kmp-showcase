package com.mk.kmpshowcase.domain.useCase.analytics

import com.mk.kmpshowcase.data.analytics.AnalyticsClient

class TrackScreenUseCase(
    private val analyticsClient: AnalyticsClient
) {
    operator fun invoke(screenName: String) {
        analyticsClient.trackScreen(screenName)
    }
}
