package mk.digital.kmpshowcase.data.di

import io.ktor.client.HttpClient
import mk.digital.kmpshowcase.data.database.AppDatabase
import mk.digital.kmpshowcase.data.database.DatabaseDriverFactory
import mk.digital.kmpshowcase.data.local.StorageLocalStore
import mk.digital.kmpshowcase.data.local.StorageLocalStoreImpl
import mk.digital.kmpshowcase.data.local.preferences.AppPreferences
import mk.digital.kmpshowcase.data.local.preferences.AppPreferencesImpl
import mk.digital.kmpshowcase.data.local.preferences.SessionPreferences
import mk.digital.kmpshowcase.data.local.preferences.SessionPreferencesImpl
import mk.digital.kmpshowcase.data.network.HttpClientProvider
import mk.digital.kmpshowcase.data.repository.BiometricRepositoryImpl
import mk.digital.kmpshowcase.data.repository.DateRepositoryImpl
import mk.digital.kmpshowcase.data.repository.LocationRepositoryImpl
import mk.digital.kmpshowcase.data.repository.SettingsRepositoryImpl
import mk.digital.kmpshowcase.data.notification.NotificationRepositoryImpl
import mk.digital.kmpshowcase.data.repository.database.AuthRepositoryImpl
import mk.digital.kmpshowcase.data.repository.database.NoteRepositoryImpl
import mk.digital.kmpshowcase.data.repository.storage.StorageRepositoryImpl
import mk.digital.kmpshowcase.data.repository.user.UserClient
import mk.digital.kmpshowcase.data.repository.user.UserClientImpl
import mk.digital.kmpshowcase.data.repository.user.UserRepositoryImpl
import mk.digital.kmpshowcase.di.Qualifiers.app
import mk.digital.kmpshowcase.di.Qualifiers.session
import mk.digital.kmpshowcase.domain.repository.AuthRepository
import mk.digital.kmpshowcase.domain.repository.BiometricRepository
import mk.digital.kmpshowcase.domain.repository.DateRepository
import mk.digital.kmpshowcase.domain.repository.LocationRepository
import mk.digital.kmpshowcase.domain.repository.NoteRepository
import mk.digital.kmpshowcase.domain.repository.NotificationRepository
import mk.digital.kmpshowcase.domain.repository.SettingsRepository
import mk.digital.kmpshowcase.domain.repository.StorageRepository
import mk.digital.kmpshowcase.domain.repository.UserRepository
import mk.digital.kmpshowcase.util.Logger
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::Logger)
    singleOf(::UserClientImpl) { bind<UserClient>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    single { provideHttpClient() }

    // Qualified preferences - need explicit qualifier
    single<SessionPreferences> { SessionPreferencesImpl(get(session)) }
    single<AppPreferences> { AppPreferencesImpl(get(app)) }

    singleOf(::StorageLocalStoreImpl) { bind<StorageLocalStore>() }
    singleOf(::StorageRepositoryImpl) { bind<StorageRepository>() }
    singleOf(::SettingsRepositoryImpl) { bind<SettingsRepository>() }
    singleOf(::LocationRepositoryImpl) { bind<LocationRepository>() }
    singleOf(::BiometricRepositoryImpl) { bind<BiometricRepository>() }
    singleOf(::DateRepositoryImpl) { bind<DateRepository>() }
    singleOf(::NoteRepositoryImpl) { bind<NoteRepository>() }
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::NotificationRepositoryImpl) { bind<NotificationRepository>() }

    // Database - needs special factory
    single { AppDatabase(get<DatabaseDriverFactory>().createDriver()) }
}

fun provideHttpClient(): HttpClient = HttpClientProvider().create()
