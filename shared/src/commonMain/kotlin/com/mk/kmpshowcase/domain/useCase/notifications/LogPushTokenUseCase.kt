package com.mk.kmpshowcase.domain.useCase.notifications

import com.mk.kmpshowcase.domain.repository.PushNotificationService
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class LogPushTokenUseCase(
    private val pushNotificationService: PushNotificationService
) : UseCase<None, Unit>() {
    override suspend fun run(params: None) = pushNotificationService.logToken()
}
