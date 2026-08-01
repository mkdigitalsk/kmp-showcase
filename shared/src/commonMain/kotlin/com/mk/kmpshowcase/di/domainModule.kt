package com.mk.kmpshowcase.di

import com.mk.kmpshowcase.domain.useCase.GetUsersUseCase
import com.mk.kmpshowcase.domain.useCase.TrackButtonClickUseCase
import com.mk.kmpshowcase.domain.useCase.analytics.TrackScreenUseCase
import com.mk.kmpshowcase.domain.useCase.auth.LoginUseCase
import com.mk.kmpshowcase.domain.useCase.auth.LoginWithTokenUseCase
import com.mk.kmpshowcase.domain.useCase.auth.LogoutUseCase
import com.mk.kmpshowcase.domain.useCase.auth.RegisterUserUseCase
import com.mk.kmpshowcase.domain.useCase.biometric.AuthenticateWithBiometricUseCase
import com.mk.kmpshowcase.domain.useCase.biometric.IsBiometricEnabledUseCase
import com.mk.kmpshowcase.domain.useCase.calendar.GetTodayDateUseCase
import com.mk.kmpshowcase.domain.useCase.flashlight.IsFlashlightAvailableUseCase
import com.mk.kmpshowcase.domain.useCase.flashlight.ToggleFlashlightUseCase
import com.mk.kmpshowcase.domain.useCase.flashlight.TurnOffFlashlightUseCase
import com.mk.kmpshowcase.domain.useCase.location.GetLastKnownLocationUseCase
import com.mk.kmpshowcase.domain.useCase.location.ObserveLocationUpdatesUseCase
import com.mk.kmpshowcase.domain.useCase.notes.DeleteAllNotesUseCase
import com.mk.kmpshowcase.domain.useCase.notes.DeleteNoteUseCase
import com.mk.kmpshowcase.domain.useCase.notes.InsertNoteUseCase
import com.mk.kmpshowcase.domain.useCase.notes.ObserveNotesUseCase
import com.mk.kmpshowcase.domain.useCase.notes.SearchNotesUseCase
import com.mk.kmpshowcase.domain.useCase.notes.UpdateNoteUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.CancelAllNotificationsUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.GetPushPermissionStatusUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.LogPushTokenUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.ObservePushNotificationsUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.ObservePushTokenUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.RefreshPushTokenUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.ShowLocalNotificationUseCase
import com.mk.kmpshowcase.domain.useCase.settings.GetThemeModeUseCase
import com.mk.kmpshowcase.domain.useCase.settings.SetThemeModeUseCase
import com.mk.kmpshowcase.domain.useCase.storage.ClearCacheUseCase
import com.mk.kmpshowcase.domain.useCase.storage.LoadStorageDataUseCase
import com.mk.kmpshowcase.domain.useCase.storage.ObserveStorageDataUseCase
import com.mk.kmpshowcase.domain.useCase.storage.SetPersistentCounterUseCase
import com.mk.kmpshowcase.domain.useCase.storage.SetSessionCounterUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::TrackScreenUseCase)
    factoryOf(::TrackButtonClickUseCase)
    factoryOf(::GetUsersUseCase)
    factoryOf(::LoadStorageDataUseCase)
    factoryOf(::ObserveStorageDataUseCase)
    factoryOf(::SetSessionCounterUseCase)
    factoryOf(::SetPersistentCounterUseCase)
    factoryOf(::ClearCacheUseCase)
    factoryOf(::GetThemeModeUseCase)
    factoryOf(::SetThemeModeUseCase)
    factoryOf(::ObserveNotesUseCase)
    factoryOf(::SearchNotesUseCase)
    factoryOf(::InsertNoteUseCase)
    factoryOf(::UpdateNoteUseCase)
    factoryOf(::DeleteNoteUseCase)
    factoryOf(::DeleteAllNotesUseCase)
    factoryOf(::GetTodayDateUseCase)
    factoryOf(::IsBiometricEnabledUseCase)
    factoryOf(::AuthenticateWithBiometricUseCase)
    factoryOf(::LoginUseCase)
    factoryOf(::LoginWithTokenUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::RegisterUserUseCase)

    factoryOf(::GetLastKnownLocationUseCase)
    factoryOf(::ObserveLocationUpdatesUseCase)

    factoryOf(::IsFlashlightAvailableUseCase)
    factoryOf(::ToggleFlashlightUseCase)
    factoryOf(::TurnOffFlashlightUseCase)

    factoryOf(::GetPushPermissionStatusUseCase)
    factoryOf(::ObservePushTokenUseCase)
    factoryOf(::ObservePushNotificationsUseCase)
    factoryOf(::RefreshPushTokenUseCase)
    factoryOf(::LogPushTokenUseCase)
    factoryOf(::ShowLocalNotificationUseCase)
    factoryOf(::CancelAllNotificationsUseCase)

}
