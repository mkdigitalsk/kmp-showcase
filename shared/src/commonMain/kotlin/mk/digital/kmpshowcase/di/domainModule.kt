package mk.digital.kmpshowcase.di

import mk.digital.kmpshowcase.domain.useCase.GetUsersUseCase
import mk.digital.kmpshowcase.domain.useCase.TrackButtonClickUseCase
import mk.digital.kmpshowcase.domain.useCase.storage.ClearCacheUseCase
import mk.digital.kmpshowcase.domain.useCase.storage.LoadStorageDataUseCase
import mk.digital.kmpshowcase.domain.useCase.storage.ObserveStorageDataUseCase
import mk.digital.kmpshowcase.domain.useCase.storage.SetPersistentCounterUseCase
import mk.digital.kmpshowcase.domain.useCase.storage.SetSessionCounterUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::TrackButtonClickUseCase)
    singleOf(::GetUsersUseCase)
    singleOf(::LoadStorageDataUseCase)
    singleOf(::ObserveStorageDataUseCase)
    singleOf(::SetSessionCounterUseCase)
    singleOf(::SetPersistentCounterUseCase)
    singleOf(::ClearCacheUseCase)
}
