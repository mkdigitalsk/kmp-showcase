package mk.digital.kmpshowcase.data.di

import io.ktor.client.HttpClient
import mk.digital.kmpshowcase.data.local.StorageLocalStore
import mk.digital.kmpshowcase.data.local.StorageLocalStoreImpl
import mk.digital.kmpshowcase.data.local.preferences.AppPreferences
import mk.digital.kmpshowcase.data.local.preferences.AppPreferencesImpl
import mk.digital.kmpshowcase.data.local.preferences.SessionPreferences
import mk.digital.kmpshowcase.data.local.preferences.SessionPreferencesImpl
import mk.digital.kmpshowcase.data.network.HttpClientProvider
import mk.digital.kmpshowcase.data.repository.storage.StorageRepositoryImpl
import mk.digital.kmpshowcase.data.repository.user.UserClient
import mk.digital.kmpshowcase.data.repository.user.UserClientImpl
import mk.digital.kmpshowcase.data.repository.user.UserRepositoryImpl
import mk.digital.kmpshowcase.di.Qualifiers.app
import mk.digital.kmpshowcase.di.Qualifiers.session
import mk.digital.kmpshowcase.domain.repository.StorageRepository
import mk.digital.kmpshowcase.domain.repository.UserRepository
import org.koin.dsl.module

val dataModule = module {
    single<UserClient> { UserClientImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single { provideHttpClient() }

    single<SessionPreferences> { SessionPreferencesImpl(get(session)) }
    single<AppPreferences> { AppPreferencesImpl(get(app)) }
    single<StorageLocalStore> { StorageLocalStoreImpl(get(), get()) }
    single<StorageRepository> { StorageRepositoryImpl(get()) }
}

fun provideHttpClient(): HttpClient = HttpClientProvider().create()
