import SwiftUI
import shared
import FirebaseCore
import FirebaseCrashlytics
import FirebaseMessaging
import UserNotifications


class AppDelegate : NSObject, UIApplicationDelegate, ObservableObject, UNUserNotificationCenterDelegate, MessagingDelegate {

    private var buildTypeString: String {
        Bundle.main.object(forInfoDictionaryKey: "BuildType") as? String ?? "debug"
    }

    private var versionName: String {
        Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String ?? "1.0"
    }

    private var versionCode: String {
        Bundle.main.object(forInfoDictionaryKey: "CFBundleVersion") as? String ?? "1"
    }

    private lazy var appConfig: AppConfig = {
        return AppConfig(
            buildType: BuildType.companion.from(name: buildTypeString),
            versionName: versionName,
            versionCode: versionCode
        )
    }()

    override init() {
        super.init()
        KoinKt.doInitKoin(appConfig: appConfig)
        PushNotificationBridge.setup()
    }

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        setupCrashlytics()
        setupPushNotifications(application: application)
        return true
    }

    private func setupCrashlytics() {
        IOSAnalyticsClient.companion.exceptionHandler = { message, stackTrace in
            let error = NSError(
                domain: "KMPException",
                code: 0,
                userInfo: [
                    NSLocalizedDescriptionKey: message,
                    "stackTrace": stackTrace
                ]
            )
            Crashlytics.crashlytics().record(error: error)
        }

        IOSAnalyticsClient.companion.logHandler = { message in
            Crashlytics.crashlytics().log(message)
        }
    }

    private func setupPushNotifications(application: UIApplication) {
        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self
        application.registerForRemoteNotifications()
    }

    // MARK: - UNUserNotificationCenterDelegate

    // Handle foreground notifications
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        let userInfo = notification.request.content.userInfo
        handleNotification(userInfo: userInfo)
        // Show banner even when app is in foreground
        completionHandler([.banner, .sound, .badge])
    }

    // Handle notification tap
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo
        handleNotification(userInfo: userInfo)

        if let deepLink = userInfo["deep_link"] as? String {
            IOSPushNotificationService.companion.onDeepLinkReceived(deepLink: deepLink)
        }

        completionHandler()
    }

    // MARK: - MessagingDelegate

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        if let token = fcmToken {
            print("FCM Token: \(token)")
            IOSPushNotificationService.companion.onTokenReceived(token: token)
        }
    }

    // MARK: - APNs Token

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }

    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register for remote notifications: \(error)")
    }

    // MARK: - Helper

    private func handleNotification(userInfo: [AnyHashable: Any]) {
        let title = (userInfo["aps"] as? [String: Any])?["alert"] as? String
            ?? ((userInfo["aps"] as? [String: Any])?["alert"] as? [String: Any])?["title"] as? String
        let body = ((userInfo["aps"] as? [String: Any])?["alert"] as? [String: Any])?["body"] as? String
        let deepLink = userInfo["deep_link"] as? String

        var data: [String: String] = [:]
        for (key, value) in userInfo {
            if let keyString = key as? String, let valueString = value as? String {
                data[keyString] = valueString
            }
        }

        IOSPushNotificationService.companion.onNotificationReceived(
            title: title,
            body: body,
            data: data,
            deepLink: deepLink
        )
    }
}
