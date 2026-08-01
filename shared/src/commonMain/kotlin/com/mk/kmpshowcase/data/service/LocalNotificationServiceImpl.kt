package com.mk.kmpshowcase.data.service

import com.mk.kmpshowcase.domain.model.Notification
import com.mk.kmpshowcase.domain.repository.LocalNotificationService

expect class LocalNotificationServiceImpl : LocalNotificationService {
    override fun showNotification(notification: Notification)
    override fun cancelNotification(id: String)
    override fun cancelAllNotifications()
}
