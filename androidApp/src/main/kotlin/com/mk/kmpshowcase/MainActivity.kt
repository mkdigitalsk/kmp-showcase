package com.mk.kmpshowcase

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mk.kmpshowcase.data.push.AndroidPushNotificationService
import com.mk.kmpshowcase.data.service.LocalNotificationServiceImpl
import com.mk.kmpshowcase.domain.repository.PushNotificationService
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), AppLocaleProvider by AppLocaleProvider.Impl() {

    private val pushService: PushNotificationService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
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
