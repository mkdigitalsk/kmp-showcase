package com.mk.kmpshowcase.di

import com.mk.kmpshowcase.data.analytics.AnalyticsClient
import com.mk.kmpshowcase.data.analytics.IOSAnalyticsClient
import com.mk.kmpshowcase.util.IosLogger
import com.mk.kmpshowcase.util.Logger
import com.mk.kmpshowcase.data.client.BiometricClient
import com.mk.kmpshowcase.data.client.BiometricClientImpl
import com.mk.kmpshowcase.data.client.FlashlightClient
import com.mk.kmpshowcase.data.client.FlashlightClientImpl
import com.mk.kmpshowcase.data.local.database.DatabaseDriverFactory
import com.mk.kmpshowcase.data.local.preferences.Preferences
import com.mk.kmpshowcase.data.local.preferences.PreferencesImpl
import com.mk.kmpshowcase.data.client.LocationClient
import com.mk.kmpshowcase.data.client.LocationClientImpl
import com.mk.kmpshowcase.data.push.IOSPushNotificationService
import com.mk.kmpshowcase.data.service.LocalNotificationServiceImpl
import com.mk.kmpshowcase.di.Qualifiers.app
import com.mk.kmpshowcase.di.Qualifiers.session
import com.mk.kmpshowcase.domain.repository.LocalNotificationService
import com.mk.kmpshowcase.domain.repository.PushNotificationService
import com.mk.kmpshowcase.presentation.base.router.ExternalRouter
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
    singleOf(::IosLogger) { bind<Logger>() }

    singleOf(::LocalNotificationServiceImpl) { bind<LocalNotificationService>() }
    single<PushNotificationService> {
        IOSPushNotificationService(get()).also {
            IOSPushNotificationService.setInstance(it)
        }
    }
}
