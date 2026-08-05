package sk.mkdigital.kmpshowcase.domain.useCase.notifications

import sk.mkdigital.kmpshowcase.domain.repository.PushNotificationService
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class RefreshPushTokenUseCase(
    private val pushNotificationService: PushNotificationService
) : UseCase<None, Unit>() {
    override suspend fun run(params: None) = pushNotificationService.refreshToken()
}
