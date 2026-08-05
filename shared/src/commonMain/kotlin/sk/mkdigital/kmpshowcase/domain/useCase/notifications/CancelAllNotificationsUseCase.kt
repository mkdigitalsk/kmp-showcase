package sk.mkdigital.kmpshowcase.domain.useCase.notifications

import sk.mkdigital.kmpshowcase.domain.repository.LocalNotificationService
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class CancelAllNotificationsUseCase(
    private val localNotificationService: LocalNotificationService
) : UseCase<None, Unit>() {
    override suspend fun run(params: None) = localNotificationService.cancelAllNotifications()
}
