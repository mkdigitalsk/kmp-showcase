package sk.mkdigital.kmpshowcase.data.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import sk.mkdigital.kmpshowcase.AppConfig
import sk.mkdigital.kmpshowcase.util.Logger
import sk.mkdigital.kmpshowcase.data.database.AppDatabase
import sk.mkdigital.kmpshowcase.data.local.database.DatabaseDriverFactory
import sk.mkdigital.kmpshowcase.data.local.StorageLocalStore
import sk.mkdigital.kmpshowcase.data.local.StorageLocalStoreImpl
import sk.mkdigital.kmpshowcase.data.local.preferences.PersistentPreferences
import sk.mkdigital.kmpshowcase.data.local.preferences.PersistentPreferencesImpl
import sk.mkdigital.kmpshowcase.data.local.preferences.SessionPreferences
import sk.mkdigital.kmpshowcase.data.local.preferences.SessionPreferencesImpl
import sk.mkdigital.kmpshowcase.data.network.HttpClientProvider
import sk.mkdigital.kmpshowcase.data.client.AuthClient
import sk.mkdigital.kmpshowcase.data.client.AuthClientImpl
import sk.mkdigital.kmpshowcase.data.client.RemoteNoteClient
import sk.mkdigital.kmpshowcase.data.client.RemoteNoteClientImpl
import sk.mkdigital.kmpshowcase.data.client.UserClient
import sk.mkdigital.kmpshowcase.data.client.UserClientImpl
import sk.mkdigital.kmpshowcase.data.repository.AuthRepositoryImpl
import sk.mkdigital.kmpshowcase.data.repository.BiometricRepositoryImpl
import sk.mkdigital.kmpshowcase.data.repository.DateRepositoryImpl
import sk.mkdigital.kmpshowcase.data.repository.FlashlightRepositoryImpl
import sk.mkdigital.kmpshowcase.data.repository.LocationRepositoryImpl
import sk.mkdigital.kmpshowcase.data.repository.NoteRepositoryImpl
import sk.mkdigital.kmpshowcase.data.repository.NotificationRepositoryImpl
import sk.mkdigital.kmpshowcase.data.repository.SettingsRepositoryImpl
import sk.mkdigital.kmpshowcase.data.repository.StorageRepositoryImpl
import sk.mkdigital.kmpshowcase.data.repository.RemoteNoteRepositoryImpl
import sk.mkdigital.kmpshowcase.data.repository.UserRepositoryImpl
import sk.mkdigital.kmpshowcase.di.Qualifiers.app
import sk.mkdigital.kmpshowcase.di.Qualifiers.session
import sk.mkdigital.kmpshowcase.domain.repository.AuthRepository
import sk.mkdigital.kmpshowcase.domain.repository.BiometricRepository
import sk.mkdigital.kmpshowcase.domain.repository.DateRepository
import sk.mkdigital.kmpshowcase.domain.repository.FlashlightRepository
import sk.mkdigital.kmpshowcase.domain.repository.LocationRepository
import sk.mkdigital.kmpshowcase.domain.repository.NoteRepository
import sk.mkdigital.kmpshowcase.domain.repository.NotificationRepository
import sk.mkdigital.kmpshowcase.domain.repository.SettingsRepository
import sk.mkdigital.kmpshowcase.domain.repository.StorageRepository
import sk.mkdigital.kmpshowcase.domain.repository.RemoteNoteRepository
import sk.mkdigital.kmpshowcase.domain.repository.UserRepository
import sk.mkdigital.kmpshowcase.util.DefaultDispatcherProvider
import sk.mkdigital.kmpshowcase.util.DispatcherProvider
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::DefaultDispatcherProvider) { bind<DispatcherProvider>() }
    single { provideHttpClient(get(), get(), get()) }
    singleOf(::AuthClientImpl) { bind<AuthClient>() }
    singleOf(::UserClientImpl) { bind<UserClient>() }
    singleOf(::RemoteNoteClientImpl) { bind<RemoteNoteClient>() }
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::RemoteNoteRepositoryImpl) { bind<RemoteNoteRepository>() }

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

fun provideHttpClient(
    preferences: PersistentPreferences,
    appConfig: AppConfig,
    logger: Logger,
): HttpClient {
    val client = HttpClientProvider(appConfig.baseUrl, appConfig.buildType, logger).create()
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
