import SwiftUI
import UIKit
import shared

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate

    var body: some Scene {
        WindowGroup {
            IosAppView(appDelegate: appDelegate)
                .onOpenURL { url in
                    IOSPushNotificationService.companion.onDeepLinkReceived(deepLink: url.absoluteString)
                }
        }
    }
}

struct IosAppView: View {
    @ObservedObject
    var appDelegate: AppDelegate

    init(appDelegate: AppDelegate) {
        self.appDelegate = appDelegate
        IosShareSheet.shared.present = { text, title, url in
            ShareSheet.present(text: text, title: title, url: url)
        }
    }

    var body: some View {
        ComposeView()
            // Compose applies the system-bar insets itself; letting SwiftUI inset the view too
            // would pad the top bar twice.
            .ignoresSafeArea()
            .ignoresSafeArea(.keyboard)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}

    private func openSettings() {
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url)
        }
    }
}
