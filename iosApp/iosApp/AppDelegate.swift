import SwiftUI
import shared
import FirebaseCore
import FirebaseCrashlytics


class AppDelegate : NSObject, UIApplicationDelegate, ObservableObject {

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
    }

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        return true
    }
}
