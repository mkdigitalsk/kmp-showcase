package com.mk.kmpshowcase.domain.useCase.notifications

import kotlinx.coroutines.flow.StateFlow
import com.mk.kmpshowcase.domain.repository.PushNotificationService
import com.mk.kmpshowcase.domain.useCase.base.FlowUseCase
import com.mk.kmpshowcase.domain.useCase.base.None

class ObservePushTokenUseCase(
    private val pushNotificationService: PushNotificationService
) : FlowUseCase<None, String?>() {
    override fun run(params: None): StateFlow<String?> = pushNotificationService.token
}
