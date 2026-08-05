package sk.mkdigital.kmpshowcase.di

import sk.mkdigital.kmpshowcase.AppConfig
import sk.mkdigital.kmpshowcase.data.di.dataModule
import sk.mkdigital.kmpshowcase.presentation.di.presentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


fun initKoin(appConfig: AppConfig, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        modules(commonModule(appConfig))
        appDeclaration()
    }

// called by iOS client
@Suppress("unused")
fun initKoin(appConfig: AppConfig) = initKoin(appConfig) {}

fun commonModule(appConfig: AppConfig) = module {
    single { appConfig }
    single { appConfig.buildType }
    includes(
        listOf(
            platformModule,
            presentationModule,
            domainModule,
            dataModule
        )
    )
}

