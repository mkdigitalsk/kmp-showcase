package sk.mkdigital.kmpshowcase.domain.useCase.notifications

import kotlinx.coroutines.flow.StateFlow
import sk.mkdigital.kmpshowcase.domain.repository.PushNotificationService
import sk.mkdigital.kmpshowcase.domain.useCase.base.FlowUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.base.None

class ObservePushTokenUseCase(
    private val pushNotificationService: PushNotificationService
) : FlowUseCase<None, String?>() {
    override fun run(params: None): StateFlow<String?> = pushNotificationService.token
}
