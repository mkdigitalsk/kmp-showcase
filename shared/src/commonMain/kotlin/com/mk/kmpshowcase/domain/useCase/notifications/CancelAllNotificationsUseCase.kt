package com.mk.kmpshowcase.domain.useCase.notifications

import com.mk.kmpshowcase.domain.repository.LocalNotificationService
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class CancelAllNotificationsUseCase(
    private val localNotificationService: LocalNotificationService
) : UseCase<None, Unit>() {
    override suspend fun run(params: None) = localNotificationService.cancelAllNotifications()
}
