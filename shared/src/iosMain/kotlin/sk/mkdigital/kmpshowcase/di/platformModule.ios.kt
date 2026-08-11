package sk.mkdigital.kmpshowcase.di

import sk.mkdigital.kmpshowcase.AppConfig
import sk.mkdigital.kmpshowcase.data.analytics.AnalyticsClient
import sk.mkdigital.kmpshowcase.data.analytics.IOSAnalyticsClient
import sk.mkdigital.kmpshowcase.util.IosLogger
import sk.mkdigital.kmpshowcase.util.Logger
import sk.mkdigital.kmpshowcase.data.client.BiometricClient
import sk.mkdigital.kmpshowcase.data.client.BiometricClientImpl
import sk.mkdigital.kmpshowcase.data.client.FlashlightClient
import sk.mkdigital.kmpshowcase.data.client.FlashlightClientImpl
import sk.mkdigital.kmpshowcase.data.local.database.DatabaseDriverFactory
import sk.mkdigital.kmpshowcase.data.local.preferences.Preferences
import sk.mkdigital.kmpshowcase.data.local.preferences.PreferencesImpl
import sk.mkdigital.kmpshowcase.data.client.LocationClient
import sk.mkdigital.kmpshowcase.data.client.LocationClientImpl
import sk.mkdigital.kmpshowcase.data.push.IOSPushNotificationService
import sk.mkdigital.kmpshowcase.data.service.LocalNotificationServiceImpl
import sk.mkdigital.kmpshowcase.di.Qualifiers.app
import sk.mkdigital.kmpshowcase.di.Qualifiers.session
import sk.mkdigital.kmpshowcase.domain.repository.LocalNotificationService
import sk.mkdigital.kmpshowcase.domain.repository.PushNotificationService
import sk.mkdigital.kmpshowcase.presentation.base.router.ExternalRouter
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::ExternalRouter)
    singleOf(::DatabaseDriverFactory)

    // Qualified preferences
    single<Preferences>(session) { PreferencesImpl(session.value) }
    single<Preferences>(app) { PreferencesImpl(app.value) }

    // Platform clients
    singleOf(::LocationClientImpl) { bind<LocationClient>() }
    singleOf(::BiometricClientImpl) { bind<BiometricClient>() }
    singleOf(::FlashlightClientImpl) { bind<FlashlightClient>() }
    singleOf(::IOSAnalyticsClient) { bind<AnalyticsClient>() }
    single<Logger> { IosLogger(get(), get<AppConfig>().buildType) }

    singleOf(::LocalNotificationServiceImpl) { bind<LocalNotificationService>() }
    single<PushNotificationService> {
        IOSPushNotificationService(get(), get()).also {
            IOSPushNotificationService.setInstance(it)
        }
    }
}
