package sk.mkdigital.kmpshowcase.domain.repository

import sk.mkdigital.kmpshowcase.domain.model.Notification

interface LocalNotificationService {
    fun showNotification(notification: Notification)
    fun cancelNotification(id: String)
    fun cancelAllNotifications()
}
