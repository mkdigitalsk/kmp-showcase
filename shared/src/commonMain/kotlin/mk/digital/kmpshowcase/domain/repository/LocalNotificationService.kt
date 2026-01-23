package mk.digital.kmpshowcase.domain.repository

import mk.digital.kmpshowcase.domain.model.Notification

interface LocalNotificationService {
    fun showNotification(notification: Notification)
    fun cancelNotification(id: String)
    fun cancelAllNotifications()
}
