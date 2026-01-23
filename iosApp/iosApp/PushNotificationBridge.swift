import Foundation
import shared
import FirebaseMessaging
import UserNotifications

/// Bridge between Kotlin Multiplatform and iOS native push notification APIs
enum PushNotificationBridge {

    static func setup() {
        IOSPushNotificationService.companion.permissionStatus = getPermissionStatus
        IOSPushNotificationService.companion.requestPermission = requestPermission
        IOSPushNotificationService.companion.refreshToken = refreshToken
    }

    private static func getPermissionStatus() -> PushPermissionStatus {
        let semaphore = DispatchSemaphore(value: 0)
        var result: PushPermissionStatus = .notDetermined

        UNUserNotificationCenter.current().getNotificationSettings { settings in
            switch settings.authorizationStatus {
            case .authorized, .provisional, .ephemeral:
                result = .granted
            case .denied:
                result = .denied
            case .notDetermined:
                result = .notDetermined
            @unknown default:
                result = .notDetermined
            }
            semaphore.signal()
        }

        semaphore.wait()
        return result
    }

    private static func requestPermission() -> PushPermissionStatus {
        let semaphore = DispatchSemaphore(value: 0)
        var result: PushPermissionStatus = .denied

        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
            if let error = error {
                print("Push permission error: \(error)")
                result = .denied
            } else {
                result = granted ? .granted : .denied
            }
            semaphore.signal()
        }

        semaphore.wait()
        return result
    }

    private static func refreshToken() {
        // APNs token must be set before FCM token can be retrieved
        guard Messaging.messaging().apnsToken != nil else {
            print("APNs token not available yet, skipping FCM token refresh")
            return
        }
        Messaging.messaging().token { token, error in
            if let token = token {
                IOSPushNotificationService.companion.onTokenReceived(token: token)
            }
        }
    }
}
