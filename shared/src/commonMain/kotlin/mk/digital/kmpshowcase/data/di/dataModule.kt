package mk.digital.kmpshowcase.data.di

import io.ktor.client.HttpClient
import mk.digital.kmpshowcase.data.network.HttpClientProvider
import mk.digital.kmpshowcase.data.repository.user.UserClient
import mk.digital.kmpshowcase.data.repository.user.UserClientImpl
import mk.digital.kmpshowcase.data.repository.user.UserRepositoryImpl
import mk.digital.kmpshowcase.domain.repository.UserRepository
import org.koin.dsl.module

val dataModule = module {
    single<UserClient> { UserClientImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single { provideHttpClient() }
}

fun provideHttpClient(): HttpClient = HttpClientProvider().create()
