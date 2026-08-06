package sk.mkdigital.kmpshowcase

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import sk.mkdigital.kmpshowcase.data.push.AndroidPushNotificationService
import sk.mkdigital.kmpshowcase.data.service.LocalNotificationServiceImpl
import sk.mkdigital.kmpshowcase.domain.repository.PushNotificationService
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), AppLocaleProvider by AppLocaleProvider.Impl() {

    private val pushService: PushNotificationService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        handleDeepLinkIntent(intent)

        setContent {
            MainView(onSetLocale = ::setLocale)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLinkIntent(intent)
    }

    private fun handleDeepLinkIntent(intent: Intent?) {
        val notificationExtrasDeepLink = intent?.getStringExtra(LocalNotificationServiceImpl.EXTRA_DEEP_LINK)
        val uriDeepLink = intent?.data?.toString()
        val deepLink = notificationExtrasDeepLink ?: uriDeepLink ?: return
        (pushService as? AndroidPushNotificationService)?.onDeepLinkReceived(deepLink.trim())
    }
}
