package com.mk.kmpshowcase.domain.useCase.notifications

import kotlinx.coroutines.flow.Flow
import com.mk.kmpshowcase.domain.model.Notification
import com.mk.kmpshowcase.domain.repository.PushNotificationService
import com.mk.kmpshowcase.domain.useCase.base.FlowUseCase
import com.mk.kmpshowcase.domain.useCase.base.None

class ObservePushNotificationsUseCase(
    private val pushNotificationService: PushNotificationService
) : FlowUseCase<None, Notification>() {
    override fun run(params: None): Flow<Notification> = pushNotificationService.notifications
}
