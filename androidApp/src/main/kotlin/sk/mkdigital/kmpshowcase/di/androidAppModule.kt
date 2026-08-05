package sk.mkdigital.kmpshowcase.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import sk.mkdigital.kmpshowcase.data.analytics.AnalyticsClient
import sk.mkdigital.kmpshowcase.data.analytics.AndroidAnalyticsClient
import sk.mkdigital.kmpshowcase.data.push.AndroidPushNotificationService
import sk.mkdigital.kmpshowcase.domain.repository.PushNotificationService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val androidAppModule = module {
    single { FirebaseAnalytics.getInstance(androidContext()) }
    single { FirebaseCrashlytics.getInstance() }
    single { FirebaseMessaging.getInstance() }
    singleOf(::AndroidAnalyticsClient) { bind<AnalyticsClient>() }
    single<PushNotificationService> {
        AndroidPushNotificationService(
            context = androidContext(),
            firebaseMessaging = get(),
            notificationRepository = get(),
            analyticsClient = get()
        )
    }
}
