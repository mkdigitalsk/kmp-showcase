package sk.mkdigital.kmpshowcase.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import sk.mkdigital.kmpshowcase.data.crash.AndroidCrashReporter
import sk.mkdigital.kmpshowcase.data.crash.CrashReporter
import sk.mkdigital.kmpshowcase.data.push.AndroidPushNotificationService
import sk.mkdigital.kmpshowcase.domain.repository.PushNotificationService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val androidAppModule = module {
    single { FirebaseCrashlytics.getInstance() }
    single { FirebaseMessaging.getInstance() }
    singleOf(::AndroidCrashReporter) { bind<CrashReporter>() }
    single<PushNotificationService> {
        AndroidPushNotificationService(
            context = androidContext(),
            firebaseMessaging = get(),
            notificationRepository = get(),
            crashReporter = get()
        )
    }
}
