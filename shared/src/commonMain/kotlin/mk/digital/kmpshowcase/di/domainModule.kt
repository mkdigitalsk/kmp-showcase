package mk.digital.kmpshowcase.di

import mk.digital.kmpshowcase.domain.useCase.LoadHomeDataUseCase
import mk.digital.kmpshowcase.domain.useCase.TrackButtonClickUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::LoadHomeDataUseCase)
    singleOf(::TrackButtonClickUseCase)
}
