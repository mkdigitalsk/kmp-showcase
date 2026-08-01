package com.mk.kmpshowcase.presentation.base.router

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

actual class SettingsRouterImpl : SettingsRouter {
    actual override fun openSettings() {
        val settingsUrl = NSURL(string = UIApplicationOpenSettingsURLString)
        UIApplication.sharedApplication.openURL(
            settingsUrl,
            options = emptyMap<Any?, Any>(),
            completionHandler = null
        )
    }

    actual override fun openNotificationSettings() {
        // iOS doesn't have a direct URL for notification settings
        // Opening app settings where user can navigate to notifications
        openSettings()
    }
}
