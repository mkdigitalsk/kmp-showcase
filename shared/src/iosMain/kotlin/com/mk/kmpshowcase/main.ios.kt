package com.mk.kmpshowcase

import androidx.compose.ui.window.ComposeUIViewController
import com.mk.kmpshowcase.data.analytics.IOSAnalyticsClient
import platform.UIKit.UIViewController

@Suppress("FunctionNaming")
fun MainViewController(
    onTrackScreen: ((String) -> Unit)? = null
): UIViewController {
    IOSAnalyticsClient.screenTrackingHandler = onTrackScreen
    return ComposeUIViewController {
        MainView()
    }
}
