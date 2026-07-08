package com.mk.kmpshowcase.data.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import com.mk.kmpshowcase.AppConfig
import com.mk.kmpshowcase.data.database.AppDatabase
import com.mk.kmpshowcase.data.local.database.DatabaseDriverFactory
import com.mk.kmpshowcase.data.local.StorageLocalStore
import com.mk.kmpshowcase.data.local.StorageLocalStoreImpl
import com.mk.kmpshowcase.data.local.preferences.PersistentPreferences
import com.mk.kmpshowcase.data.local.preferences.PersistentPreferencesImpl
import com.mk.kmpshowcase.data.local.preferences.SessionPreferences
import com.mk.kmpshowcase.data.local.preferences.SessionPreferencesImpl
import com.mk.kmpshowcase.data.network.HttpClientProvider
import com.mk.kmpshowcase.data.client.AuthClient
import com.mk.kmpshowcase.data.client.AuthClientImpl
import com.mk.kmpshowcase.data.client.UserClient
import com.mk.kmpshowcase.data.client.UserClientImpl
import com.mk.kmpshowcase.data.repository.AuthRepositoryImpl
import com.mk.kmpshowcase.data.repository.BiometricRepositoryImpl
import com.mk.kmpshowcase.data.repository.DateRepositoryImpl
import com.mk.kmpshowcase.data.repository.FlashlightRepositoryImpl
import com.mk.kmpshowcase.data.repository.LocationRepositoryImpl
import com.mk.kmpshowcase.data.repository.NoteRepositoryImpl
import com.mk.kmpshowcase.data.repository.NotificationRepositoryImpl
import com.mk.kmpshowcase.data.repository.SettingsRepositoryImpl
import com.mk.kmpshowcase.data.repository.StorageRepositoryImpl
import com.mk.kmpshowcase.data.repository.UserRepositoryImpl
import com.mk.kmpshowcase.di.Qualifiers.app
import com.mk.kmpshowcase.di.Qualifiers.session
import com.mk.kmpshowcase.domain.repository.AuthRepository
import com.mk.kmpshowcase.domain.repository.BiometricRepository
import com.mk.kmpshowcase.domain.repository.DateRepository
import com.mk.kmpshowcase.domain.repository.FlashlightRepository
import com.mk.kmpshowcase.domain.repository.LocationRepository
import com.mk.kmpshowcase.domain.repository.NoteRepository
import com.mk.kmpshowcase.domain.repository.NotificationRepository
import com.mk.kmpshowcase.domain.repository.SettingsRepository
import com.mk.kmpshowcase.domain.repository.StorageRepository
import com.mk.kmpshowcase.domain.repository.UserRepository
import com.mk.kmpshowcase.util.DefaultDispatcherProvider
import com.mk.kmpshowcase.util.DispatcherProvider
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::DefaultDispatcherProvider) { bind<DispatcherProvider>() }
    single { provideHttpClient(get(), get<AppConfig>().baseUrl) }
    singleOf(::AuthClientImpl) { bind<AuthClient>() }
    singleOf(::UserClientImpl) { bind<UserClient>() }
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }

    // Qualified preferences - need explicit qualifier
    single<SessionPreferences> { SessionPreferencesImpl(get(session)) }
    single<PersistentPreferences> { PersistentPreferencesImpl(get(app)) }

    singleOf(::StorageLocalStoreImpl) { bind<StorageLocalStore>() }
    singleOf(::StorageRepositoryImpl) { bind<StorageRepository>() }
    singleOf(::SettingsRepositoryImpl) { bind<SettingsRepository>() }
    singleOf(::LocationRepositoryImpl) { bind<LocationRepository>() }
    singleOf(::BiometricRepositoryImpl) { bind<BiometricRepository>() }
    singleOf(::FlashlightRepositoryImpl) { bind<FlashlightRepository>() }
    singleOf(::DateRepositoryImpl) { bind<DateRepository>() }
    singleOf(::NoteRepositoryImpl) { bind<NoteRepository>() }
    singleOf(::NotificationRepositoryImpl) { bind<NotificationRepository>() }

    // Database - needs special factory
    single { AppDatabase(get<DatabaseDriverFactory>().createDriver()) }
}

fun provideHttpClient(preferences: PersistentPreferences, baseUrl: String): HttpClient {
    val client = HttpClientProvider(baseUrl).create()
    client.plugin(HttpSend).intercept { request ->
        val token = preferences.getToken()
        if (token != null) {
            request.headers.remove(HttpHeaders.Authorization)
            request.header(HttpHeaders.Authorization, "Bearer $token")
        }
        execute(request)
    }
    return client
}
