package com.mk.kmpshowcase.di

import com.mk.kmpshowcase.AppConfig
import com.mk.kmpshowcase.data.di.dataModule
import com.mk.kmpshowcase.presentation.di.presentationModule
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

