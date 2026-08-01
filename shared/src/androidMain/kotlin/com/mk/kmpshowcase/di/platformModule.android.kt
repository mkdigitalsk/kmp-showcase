package com.mk.kmpshowcase.di

import com.mk.kmpshowcase.data.client.BiometricClient
import com.mk.kmpshowcase.data.client.BiometricClientImpl
import com.mk.kmpshowcase.data.client.FlashlightClient
import com.mk.kmpshowcase.data.client.FlashlightClientImpl
import com.mk.kmpshowcase.data.local.database.DatabaseDriverFactory
import com.mk.kmpshowcase.data.local.preferences.Preferences
import com.mk.kmpshowcase.data.local.preferences.PreferencesImpl
import com.mk.kmpshowcase.data.client.LocationClient
import com.mk.kmpshowcase.data.client.LocationClientImpl
import com.mk.kmpshowcase.data.service.LocalNotificationServiceImpl
import com.mk.kmpshowcase.di.Qualifiers.app
import com.mk.kmpshowcase.di.Qualifiers.session
import com.mk.kmpshowcase.domain.repository.LocalNotificationService
import com.mk.kmpshowcase.presentation.base.router.ExternalRouter
import com.mk.kmpshowcase.util.AndroidLogger
import com.mk.kmpshowcase.util.Logger
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
