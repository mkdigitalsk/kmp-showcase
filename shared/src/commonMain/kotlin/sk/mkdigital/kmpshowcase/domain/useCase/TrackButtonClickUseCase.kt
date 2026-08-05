package sk.mkdigital.kmpshowcase.domain.useCase

import sk.mkdigital.kmpshowcase.data.analytics.AnalyticsClient
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class TrackButtonClickUseCase(
    private val analyticsClient: AnalyticsClient
) : UseCase<Int, Unit>() {
    override suspend fun run(params: Int) {
        analyticsClient.log("Button Clicked: $params")
    }
}
