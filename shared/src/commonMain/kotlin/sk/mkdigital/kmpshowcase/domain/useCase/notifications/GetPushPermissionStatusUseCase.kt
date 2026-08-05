package sk.mkdigital.kmpshowcase.domain.useCase.notifications

import sk.mkdigital.kmpshowcase.domain.repository.PushNotificationService
import sk.mkdigital.kmpshowcase.domain.repository.PushPermissionStatus
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class GetPushPermissionStatusUseCase(
    private val pushNotificationService: PushNotificationService
) : UseCase<None, PushPermissionStatus>() {
    override suspend fun run(params: None): PushPermissionStatus =
        pushNotificationService.getPermissionStatus()
}
