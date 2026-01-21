package mk.digital.kmpshowcase.domain.useCase

import mk.digital.kmpshowcase.domain.useCase.base.UseCase
import mk.digital.kmpshowcase.util.Logger

class TrackButtonClickUseCase : UseCase<Int, Unit>() {
    override suspend fun run(params: Int) {
        Logger.d("Button Clicked: $params")
    }
}
