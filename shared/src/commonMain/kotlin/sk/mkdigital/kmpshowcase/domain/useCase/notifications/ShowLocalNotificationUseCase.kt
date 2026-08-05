package sk.mkdigital.kmpshowcase.domain.useCase.notifications

import sk.mkdigital.kmpshowcase.domain.model.Notification
import sk.mkdigital.kmpshowcase.domain.repository.LocalNotificationService
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class ShowLocalNotificationUseCase(
    private val localNotificationService: LocalNotificationService
) : UseCase<Notification, Unit>() {
    override suspend fun run(params: Notification) = localNotificationService.showNotification(params)
}
