package com.mk.kmpshowcase.domain.useCase

import com.mk.kmpshowcase.data.analytics.AnalyticsClient
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class TrackButtonClickUseCase(
    private val analyticsClient: AnalyticsClient
) : UseCase<Int, Unit>() {
    override suspend fun run(params: Int) {
        analyticsClient.log("Button Clicked: $params")
    }
}
