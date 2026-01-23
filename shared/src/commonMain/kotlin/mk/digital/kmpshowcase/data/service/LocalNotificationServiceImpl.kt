package mk.digital.kmpshowcase.data.service

import mk.digital.kmpshowcase.domain.model.Notification
import mk.digital.kmpshowcase.domain.repository.LocalNotificationService

expect class LocalNotificationServiceImpl : LocalNotificationService {
    override fun showNotification(notification: Notification)
    override fun cancelNotification(id: String)
    override fun cancelAllNotifications()
}
