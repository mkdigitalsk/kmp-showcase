package sk.mkdigital.kmpshowcase.data.service

import sk.mkdigital.kmpshowcase.domain.model.Notification
import sk.mkdigital.kmpshowcase.domain.repository.LocalNotificationService

expect class LocalNotificationServiceImpl : LocalNotificationService {
    override fun showNotification(notification: Notification)
    override fun cancelNotification(id: String)
    override fun cancelAllNotifications()
}
