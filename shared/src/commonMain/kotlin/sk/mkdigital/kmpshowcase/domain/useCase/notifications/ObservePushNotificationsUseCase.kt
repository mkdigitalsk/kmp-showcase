package sk.mkdigital.kmpshowcase.domain.useCase.notifications

import kotlinx.coroutines.flow.Flow
import sk.mkdigital.kmpshowcase.domain.model.Notification
import sk.mkdigital.kmpshowcase.domain.repository.PushNotificationService
import sk.mkdigital.kmpshowcase.domain.useCase.base.FlowUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.base.None

class ObservePushNotificationsUseCase(
    private val pushNotificationService: PushNotificationService
) : FlowUseCase<None, Notification>() {
    override fun run(params: None): Flow<Notification> = pushNotificationService.notifications
}
