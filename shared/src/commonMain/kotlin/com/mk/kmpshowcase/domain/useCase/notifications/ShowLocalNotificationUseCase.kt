package com.mk.kmpshowcase.domain.useCase.notifications

import com.mk.kmpshowcase.domain.model.Notification
import com.mk.kmpshowcase.domain.repository.LocalNotificationService
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class ShowLocalNotificationUseCase(
    private val localNotificationService: LocalNotificationService
) : UseCase<Notification, Unit>() {
    override suspend fun run(params: Notification) = localNotificationService.showNotification(params)
}
