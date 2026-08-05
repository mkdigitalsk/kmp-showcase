package sk.mkdigital.kmpshowcase.di

import sk.mkdigital.kmpshowcase.data.client.BiometricClient
import sk.mkdigital.kmpshowcase.data.client.BiometricClientImpl
import sk.mkdigital.kmpshowcase.data.client.FlashlightClient
import sk.mkdigital.kmpshowcase.data.client.FlashlightClientImpl
import sk.mkdigital.kmpshowcase.data.local.database.DatabaseDriverFactory
import sk.mkdigital.kmpshowcase.data.local.preferences.Preferences
import sk.mkdigital.kmpshowcase.data.local.preferences.PreferencesImpl
import sk.mkdigital.kmpshowcase.data.client.LocationClient
import sk.mkdigital.kmpshowcase.data.client.LocationClientImpl
import sk.mkdigital.kmpshowcase.data.service.LocalNotificationServiceImpl
import sk.mkdigital.kmpshowcase.di.Qualifiers.app
import sk.mkdigital.kmpshowcase.di.Qualifiers.session
import sk.mkdigital.kmpshowcase.domain.repository.LocalNotificationService
import sk.mkdigital.kmpshowcase.presentation.base.router.ExternalRouter
import sk.mkdigital.kmpshowcase.util.AndroidLogger
import sk.mkdigital.kmpshowcase.util.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::ExternalRouter)

    single<Logger> { AndroidLogger(get()) }

    // Qualified preferences - need androidContext()
    single<Preferences>(session) { PreferencesImpl(androidContext(), session.value) }
    single<Preferences>(app) { PreferencesImpl(androidContext(), app.value) }

    // Platform clients - need androidContext()
    single<LocationClient> { LocationClientImpl(androidContext()) }
    single<BiometricClient> { BiometricClientImpl(androidContext()) }
    single<FlashlightClient> { FlashlightClientImpl(androidContext()) }
    single { DatabaseDriverFactory(androidContext()) }

    single<LocalNotificationService> { LocalNotificationServiceImpl(androidContext()) }
}
