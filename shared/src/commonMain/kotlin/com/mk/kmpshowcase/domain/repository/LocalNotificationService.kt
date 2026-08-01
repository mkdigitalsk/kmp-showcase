package com.mk.kmpshowcase.domain.repository

import com.mk.kmpshowcase.domain.model.Notification

interface LocalNotificationService {
    fun showNotification(notification: Notification)
    fun cancelNotification(id: String)
    fun cancelAllNotifications()
}
