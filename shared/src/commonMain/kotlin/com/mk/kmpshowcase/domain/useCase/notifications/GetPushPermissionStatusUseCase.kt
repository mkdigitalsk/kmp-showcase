package com.mk.kmpshowcase.domain.useCase.notifications

import com.mk.kmpshowcase.domain.repository.PushNotificationService
import com.mk.kmpshowcase.domain.repository.PushPermissionStatus
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class GetPushPermissionStatusUseCase(
    private val pushNotificationService: PushNotificationService
) : UseCase<None, PushPermissionStatus>() {
    override suspend fun run(params: None): PushPermissionStatus =
        pushNotificationService.getPermissionStatus()
}
