package mk.digital.kmpshowcase.di

import mk.digital.kmpshowcase.data.biometric.BiometricClient
import mk.digital.kmpshowcase.data.biometric.BiometricClientImpl
import mk.digital.kmpshowcase.data.database.DatabaseDriverFactory
import mk.digital.kmpshowcase.data.local.preferences.Preferences
import mk.digital.kmpshowcase.data.local.preferences.PreferencesImpl
import mk.digital.kmpshowcase.data.location.LocationClient
import mk.digital.kmpshowcase.data.location.LocationClientImpl
import mk.digital.kmpshowcase.data.service.LocalNotificationServiceImpl
import mk.digital.kmpshowcase.di.Qualifiers.app
import mk.digital.kmpshowcase.di.Qualifiers.session
import mk.digital.kmpshowcase.domain.repository.LocalNotificationService
import mk.digital.kmpshowcase.presentation.base.router.ExternalRouter
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::ExternalRouter)

    // Qualified preferences - need androidContext()
    single<Preferences>(session) { PreferencesImpl(androidContext(), session.value) }
    single<Preferences>(app) { PreferencesImpl(androidContext(), app.value) }

    // Platform clients - need androidContext()
    single<LocationClient> { LocationClientImpl(androidContext()) }
    single<BiometricClient> { BiometricClientImpl(androidContext()) }
    single { DatabaseDriverFactory(androidContext()) }

    single<LocalNotificationService> { LocalNotificationServiceImpl(androidContext()) }
}
