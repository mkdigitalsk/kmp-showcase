package mk.digital.kmpshowcase.data.service

import mk.digital.kmpshowcase.domain.model.Notification
import mk.digital.kmpshowcase.domain.repository.LocalNotificationService
import platform.Foundation.NSError
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

actual class LocalNotificationServiceImpl : LocalNotificationService {

    private val center: UNUserNotificationCenter
        get() = UNUserNotificationCenter.currentNotificationCenter()

    actual override fun showNotification(notification: Notification) {
        val content = UNMutableNotificationContent().apply {
            setTitle(notification.title)
            setBody(notification.message)
            setSound(UNNotificationSound.defaultSound)

            // Add deep link and data to userInfo
            val userInfo = mutableMapOf<Any?, Any?>()
            notification.deepLink?.let { userInfo["deep_link"] = it }
            notification.data.forEach { (key, value) -> userInfo[key] = value }
            if (userInfo.isNotEmpty()) {
                setUserInfo(userInfo)
            }
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = 0.1,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = notification.id,
            content = content,
            trigger = trigger
        )

        center.addNotificationRequest(request) { error: NSError? ->
            if (error != null) {
                println("Error showing notification: $error")
            } else {
                println("Notification shown: ${notification.title}")
            }
        }
    }

    actual override fun cancelNotification(id: String) {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(id))
        center.removeDeliveredNotificationsWithIdentifiers(listOf(id))
    }

    actual override fun cancelAllNotifications() {
        center.removeAllPendingNotificationRequests()
        center.removeAllDeliveredNotifications()
    }
}