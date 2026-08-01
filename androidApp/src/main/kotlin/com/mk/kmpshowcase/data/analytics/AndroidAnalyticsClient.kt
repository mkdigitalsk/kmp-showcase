package com.mk.kmpshowcase.data.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class AndroidAnalyticsClient(
    private val analytics: FirebaseAnalytics,
    private val crashlytics: FirebaseCrashlytics
    ) : AnalyticsClient {

    override fun trackScreen(screenName: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        })
    }

    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }
}
